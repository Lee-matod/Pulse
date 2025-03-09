package leematod.pulse.ui.layouts;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.components.Sidebar;

public abstract class Layout extends RelativeLayout implements Styleable {
    public final @NonNull Sidebar sidebar;

    public Layout(Context context) {
        super(context);
        this.setPalette(ColorPalette.current);

        this.sidebar = new Sidebar(context);
        this.sidebar.setCallback(this::sidebarCallback);
        this.addView(this.sidebar);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.setBackgroundColor(palette.background);
    }

    @Nullable
    public abstract View sidebarCallback(int pos, @NonNull String name);
}
