package leematod.pulse.ui.layouts.artist;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.filter;
import static leematod.pulse.Utils.pixels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import leematod.pulse.MainActivity;
import leematod.pulse.R;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.models.ArtistItem;
import leematod.pulse.models.ItemGroup;
import leematod.pulse.models.SongItem;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.DynamicRecyclerAdapter;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.cards.CardDisplay;
import leematod.pulse.ui.components.ContainerizedLayout;
import leematod.pulse.ui.components.Sidebar;
import leematod.pulse.ui.effects.Pressable;

import java.util.ArrayList;
import java.util.List;

public class ArtistScreen extends ContainerizedLayout
        implements ArtistLayout.CompatibleScreen, Styleable {
    public static final int ICON = 48;
    public static final int PADDING = 8;
    public static final int BANNER_HEIGHT = Sidebar.WIDTH + Sidebar.TAB_SEPARATION;

    public final @NonNull TextView name;
    public final @NonNull ImageView banner;
    public final @NonNull TextView description;
    public final @NonNull ImageView shuffleButton;
    public final @NonNull ImageView radioButton;
    public final @NonNull RecyclerView container;
    protected final @NonNull MainActivity activity;
    protected final @NonNull ContainerAdapter adapter;
    protected final @NonNull List<ItemGroup> items;

    public ArtistScreen(@NonNull Context context) {
        super(context);
        this.items = new ArrayList<>();
        this.adapter = new ContainerAdapter();
        this.adapter.setStartupDummies(3);
        this.activity = (MainActivity) context;
        this.banner = ensureId(new ImageView(context));
        this.name = new TextView(context);
        this.description = new TextView(context);
        this.shuffleButton = ensureId(new ImageView(context));
        this.radioButton = ensureId(new ImageView(context));
        this.container = new RecyclerView(context);
        this.container.setLayoutManager(new LinearLayoutManager(context));
        this.container.setAdapter(this.adapter);

        this.createBanner();
        this.createSubtitle();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -2);
        params.topMargin = pixels(PADDING * 2);
        this.addView(this.container, params);

        this.setPalette(ColorPalette.current);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.name.setTextColor(palette.text);
        this.description.setTextColor(palette.textSecondary);
        this.shuffleButton.setColorFilter(palette.background);
        this.radioButton.setColorFilter(palette.secondary);

        Pressable radioBackground = new Pressable(palette.onAccent);
        radioBackground.setAnimationOrigin(Pressable.OriginType.CLICK);
        this.radioButton.setBackground(radioBackground);

        GradientDrawable gradient =
                new GradientDrawable(
                        GradientDrawable.Orientation.BOTTOM_TOP,
                        new int[] {palette.background, Color.TRANSPARENT, Color.TRANSPARENT});
        this.banner.setForeground(gradient);

        ShapeDrawable oval = new ShapeDrawable(new OvalShape());
        oval.setTint(palette.primary);
        Pressable pressable = new Pressable(palette.onAccent);
        pressable.isLayered();
        pressable.setAnimationOrigin(Pressable.OriginType.CLICK);
        LayerDrawable drawable = new LayerDrawable(new Drawable[] {oval, pressable});
        this.shuffleButton.setBackground(drawable);
    }

    @Override
    public void setArtist(@NonNull String artistId) {
        this.activity
                .client
                .artist(artistId)
                .thenAccept((item) -> this.post(() -> this.setArtist(item)));
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void setArtist(@NonNull ArtistItem item) {
        this.items.clear();
        this.items.addAll(filter(item.items, (i) -> !i.items.isEmpty()));
        this.adapter.notifyDataSetChanged();
        this.name.setText(item.name);
        if (item.thumbnail != null) {
            this.activity
                    .client
                    .render(item.thumbnail.banner(this.getWidth(), pixels(BANNER_HEIGHT)))
                    .into(this.banner);
        }
        if (item.description != null) {
            this.description.setText(item.description);
        }
    }

    private void createBanner() {
        RelativeLayout.LayoutParams imageLP =
                new RelativeLayout.LayoutParams(-1, pixels(BANNER_HEIGHT));
        this.addView(this.banner, imageLP);

        int pad = pixels(PADDING * 2);
        RelativeLayout.LayoutParams nameLP = new RelativeLayout.LayoutParams(-1, -2);
        nameLP.setMarginStart(pad);
        nameLP.setMarginEnd(pad);
        nameLP.addRule(RelativeLayout.ALIGN_BOTTOM, this.banner.getId());
        this.name.setTextSize(32);
        this.name.setTypeface(Typeface.DEFAULT_BOLD);
        this.layout.addView(this.name, nameLP);
    }

    private void createSubtitle() {
        int pad = pixels(PADDING);
        RelativeLayout.LayoutParams descLP = new RelativeLayout.LayoutParams(-2, -2);
        descLP.setMarginEnd(pad);
        descLP.addRule(RelativeLayout.ALIGN_TOP, this.shuffleButton.getId());
        descLP.addRule(RelativeLayout.ALIGN_PARENT_START);
        descLP.addRule(RelativeLayout.ALIGN_BOTTOM, this.shuffleButton.getId());
        descLP.addRule(RelativeLayout.START_OF, this.radioButton.getId());
        this.description.setTextSize(14);
        this.description.setEllipsize(TextUtils.TruncateAt.END);
        this.addView(this.description, descLP);

        RelativeLayout.LayoutParams iconLP =
                new RelativeLayout.LayoutParams(pixels(ICON), pixels(ICON));
        iconLP.topMargin = pad;
        iconLP.setMarginEnd(pad);
        iconLP.addRule(RelativeLayout.BELOW, this.banner.getId());

        RelativeLayout.LayoutParams shuffleLP = new RelativeLayout.LayoutParams(iconLP);
        shuffleLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        this.shuffleButton.setImageResource(R.drawable.shuffle);
        this.shuffleButton.setPaddingRelative(pad, pad, pad, pad);
        this.shuffleButton.setClickable(true);

        RelativeLayout.LayoutParams radioLP = new RelativeLayout.LayoutParams(iconLP);
        radioLP.addRule(RelativeLayout.START_OF, this.shuffleButton.getId());
        this.radioButton.setImageResource(R.drawable.radio);
        this.radioButton.setPaddingRelative(pad, pad, pad, pad);
        this.radioButton.setClickable(true);
        this.layout.addView(this.shuffleButton, shuffleLP);
        this.layout.addView(this.radioButton, radioLP);
    }

    public class ContainerAdapter extends DynamicRecyclerAdapter<ItemGroup, RelativeLayout> {

        @Override
        public int getRealItemCount() {
            return ArtistScreen.this.items.size();
        }

        @NonNull
        @Override
        public ItemGroup getItemAt(int position) {
            return ArtistScreen.this.items.get(position);
        }

        @NonNull
        @Override
        public ItemGroup getDummyAt(int position) {
            ItemGroup group = new ItemGroup("Container");
            switch (position) {
                case 0:
                    for (int i = 0; i < 4; i++) {
                        group.items.add(new SongItem("Song Item", null, null, null));
                    }
                    break;
                case 1:
                    for (int i = 0; i < 5; i++) {
                        group.items.add(new ArtistItem("Artist Item", null, null, null));
                    }
                    break;
                default:
                    for (int i = 0; i < 5; i++) {
                        group.items.add(new AlbumItem("Album Item", null, null, null));
                    }
                    break;
            }
            return group;
        }

        @Override
        public void bindView(
                @NonNull ItemGroup group,
                @NonNull RelativeLayout view,
                int position,
                boolean isDummy) {
            view.removeAllViews();

            Context context = view.getContext();
            RelativeLayout.LayoutParams displayParams = new RelativeLayout.LayoutParams(-2, -2);

            CardDisplay display = new CardDisplay(context);
            display.setItems(group);

            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-2, -2);
            titleParams.topMargin = pixels(8);
            titleParams.bottomMargin = pixels(8);

            TextView title = ensureId(new TextView(context));
            title.setText(group.name);
            title.setTextSize(24);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(ColorPalette.current.text);
            title.setBackgroundColor(ColorPalette.current.background);
            displayParams.addRule(RelativeLayout.BELOW, title.getId());

            view.addView(title, titleParams);
            view.addView(display, displayParams);
        }

        @NonNull
        @Override
        public DynamicViewHolder<RelativeLayout> onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            return new DynamicViewHolder<>(new RelativeLayout(parent.getContext()));
        }
    }
}
