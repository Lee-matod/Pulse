package leematod.pulse.ui.components;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import leematod.pulse.MainActivity;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.effects.Pressable;

import java.util.function.BiFunction;

public class Sidebar extends RelativeLayout implements Styleable {
    public static final int WIDTH = 80;
    public static final int TAB_SEPARATION = 100;
    public static final int PADDING = 8;
    protected @Nullable BiFunction<Integer, String, View> callback;
    private boolean isAnimating = false;
    private @Nullable ImageView icon;
    private @Nullable View view;
    private int activeTab = -1;

    public Sidebar(@NonNull Context context) {
        super(context);

        ensureId(this);
        LayoutParams layoutParams = new LayoutParams(pixels(WIDTH), -1);
        layoutParams.addRule(ALIGN_PARENT_START);
        layoutParams.addRule(ALIGN_PARENT_TOP);
        layoutParams.addRule(ALIGN_PARENT_BOTTOM);
        this.setLayoutParams(layoutParams);
    }

    public void setCallback(@Nullable BiFunction<Integer, String, View> callback) {
        this.callback = callback;
    }

    @CanIgnoreReturnValue
    public int addTab(@StringRes int name, @DrawableRes int icon) {
        Context context = this.getContext();

        int index = this.getChildCount();

        Tab tab = new Tab(context);
        tab.setText(name);
        tab.setIcon(icon);
        tab.setOnClickListener(v -> this.setActiveTab(index));
        tab.setY(pixels(TAB_SEPARATION) * (index + 1) + pixels(WIDTH));
        tab.setPaddingRelative(0, 0, 0, pixels(8));
        tab.setRotation(-90);

        this.addView(tab, new LayoutParams(-1, -2));
        return index;
    }

    public void setIcon(@DrawableRes int icon) {
        Drawable drawable = this.getContext().getTheme().getDrawable(icon);
        if (this.icon == null) {
            LayoutParams layoutParams = new LayoutParams(-2, -2);
            layoutParams.addRule(ALIGN_PARENT_TOP);
            layoutParams.addRule(CENTER_HORIZONTAL);
            layoutParams.topMargin = pixels(WIDTH);

            int pad = pixels(PADDING);
            this.icon = new ImageView(this.getContext());
            this.icon.setClickable(true);
            this.icon.setPaddingRelative(pad, pad, pad, pad);
            this.addView(this.icon, layoutParams);
        }
        this.icon.setImageDrawable(drawable);
        this.setPalette(ColorPalette.current);
    }

    public void setIcon(@DrawableRes int icon, @NonNull Runnable callback) {
        this.setIcon(icon);
        if (this.icon != null) {
            this.icon.setOnClickListener(
                    v -> {
                        if (!this.isAnimating) {
                            callback.run();
                        }
                    });
        }
    }

    public void setActiveTab(int index) {
        if (this.isAnimating) {
            return;
        }

        this.isAnimating = true;
        Tab selectedTab = null;
        int selectedTabIndex = -1;
        int tabIndex = 0;
        for (int i = 0; i < this.getChildCount(); i++) {
            View view = this.getChildAt(i);
            if (!(view instanceof Tab tab)) {
                continue;
            }
            if (tabIndex == index) {
                selectedTab = tab;
                selectedTabIndex = tabIndex;
                tab.text.setTextColor(ColorPalette.current.text);
                tab.image.animate().translationY(0).start();
            } else {
                tab.text.setTextColor(ColorPalette.current.textDisabled);
                tab.image.animate().translationY(-pixels(24)).start();
            }
            tabIndex++;
        }

        if (selectedTab == null || selectedTabIndex == this.activeTab || this.callback == null) {
            this.isAnimating = false;
            return;
        }
        View newView = this.callback.apply(selectedTabIndex, (String) selectedTab.text.getText());
        if (newView == null) {
            this.isAnimating = false;
            return;
        }

        Rect bounds =
                ((MainActivity) this.getContext())
                        .getWindowManager()
                        .getCurrentWindowMetrics()
                        .getBounds();
        int height = bounds.height();
        int width = bounds.width();

        ViewGroup parent = (ViewGroup) this.getParent();

        LayoutParams layoutParams = new LayoutParams(width - pixels(WIDTH), -1);
        layoutParams.addRule(END_OF, this.getId());
        parent.addView(newView, layoutParams);

        if (this.activeTab < 0 || this.view == null) {
            this.view = newView;
            this.isAnimating = false;
            this.activeTab = selectedTabIndex;
            return;
        }

        int childDirection = this.activeTab > selectedTabIndex ? -1 : 1;
        int oldChildDirection = childDirection * -1;

        newView.setY(height * childDirection);
        newView.animate().y(0).setDuration(300).start();
        this.view
                .animate()
                .y(height * oldChildDirection)
                .setDuration(300)
                .withEndAction(
                        () -> {
                            parent.removeView(this.view);
                            this.view = newView;
                            this.isAnimating = false;
                        })
                .start();
        this.activeTab = selectedTabIndex;
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        if (this.icon != null) {
            this.icon.setColorFilter(palette.text);
            this.icon.setBackground(new Pressable(palette.onAccent));
        }
    }

    protected static class Tab extends RelativeLayout implements Styleable {
        public final @NonNull TextView text;
        public final @NonNull ImageView image;

        public Tab(@NonNull Context context) {
            super(context);

            LayoutParams imageLP = new LayoutParams(-2, -2);
            imageLP.addRule(CENTER_HORIZONTAL);
            this.image = ensureId(new ImageView(context));

            LayoutParams textLP = new LayoutParams(-2, -2);
            textLP.addRule(CENTER_HORIZONTAL);
            textLP.addRule(BELOW, this.image.getId());
            this.text = new TextView(context);
            this.text.setTypeface(Typeface.DEFAULT_BOLD);

            this.setPalette(ColorPalette.current);
            this.addView(this.text, textLP);
            this.addView(this.image, imageLP);
        }

        public void setText(@StringRes int res) {
            this.text.setText(res);
        }

        public void setIcon(@DrawableRes int res) {
            this.image.setImageDrawable(this.getContext().getTheme().getDrawable(res));
        }

        @Override
        public void setPalette(@NonNull ColorPalette palette) {
            this.image.setColorFilter(palette.text);

            Pressable drawable = new Pressable(palette.onAccent);
            drawable.setPressType(Pressable.HORIZONTAL);
            drawable.setOriginType(Pressable.TOUCH);
            this.setBackground(drawable);
        }
    }
}
