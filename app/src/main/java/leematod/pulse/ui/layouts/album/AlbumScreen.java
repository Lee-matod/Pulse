package leematod.pulse.ui.layouts.album;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import leematod.pulse.MainActivity;
import leematod.pulse.R;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.cards.SongList;
import leematod.pulse.ui.components.ContainerizedLayout;
import leematod.pulse.ui.components.Sidebar;
import leematod.pulse.ui.effects.CircularBitmapTarget;
import leematod.pulse.ui.effects.Pressable;
import leematod.pulse.ui.effects.shimmer.ShimmerHost;

public class AlbumScreen extends ContainerizedLayout implements Styleable {
    public static final int THUMBNAIL = 164;
    public static final int PADDING = 16;
    public static final int ICON = PADDING * 3;
    public final @NonNull TextView name;
    public final @NonNull ShapeableImageView thumbnail;
    public final @NonNull TextView artistName;
    public final @NonNull ImageView artistThumbnail;
    public final @NonNull ImageView playButton;
    public final @NonNull ImageView shuffleButton;
    public final @NonNull SongList songList;
    protected final @NonNull ShimmerHost shimmer;
    protected final @NonNull MainActivity activity;
    private final @NonNull RelativeLayout albumLayout;
    private @Nullable TextView lastSecondaryText;

    public AlbumScreen(@NonNull Context context) {
        super(context);

        this.activity = (MainActivity) context;
        this.name = ensureId(new TextView(context));
        this.thumbnail = ensureId(new ShapeableImageView(context));

        RelativeLayout.LayoutParams albumLayoutLP = new RelativeLayout.LayoutParams(-1, -2);
        albumLayoutLP.topMargin = pixels(Sidebar.WIDTH);
        this.albumLayout = this.createAlbum(context);
        this.albumLayout.setLayoutParams(albumLayoutLP);
        this.shimmer = ensureId(new ShimmerHost(this.albumLayout));
        this.addView(this.shimmer);

        RelativeLayout.LayoutParams playerLP = new RelativeLayout.LayoutParams(-2, -2);
        playerLP.setMarginStart(pixels(THUMBNAIL));
        playerLP.addRule(RelativeLayout.BELOW, this.shimmer.getId());
        playerLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        this.playButton = new ImageView(context);
        this.shuffleButton = new ImageView(context);
        RelativeLayout player = ensureId(this.createPlayer(context));
        this.addView(player, playerLP);

        RelativeLayout.LayoutParams artistLP =
                new RelativeLayout.LayoutParams(pixels(THUMBNAIL), -2);
        artistLP.topMargin = pixels(PADDING);
        artistLP.addRule(RelativeLayout.ALIGN_TOP, player.getId());
        artistLP.addRule(RelativeLayout.ALIGN_BOTTOM, player.getId());
        artistLP.addRule(RelativeLayout.ALIGN_PARENT_START);
        this.artistName = ensureId(new TextView(context));
        this.artistThumbnail = ensureId(new ImageView(context));
        this.addView(this.createArtist(context), artistLP);

        RelativeLayout.LayoutParams songListLP = new RelativeLayout.LayoutParams(-1, -2);
        this.songList = new SongList(context);
        this.songList.enumerate();
        this.addView(this.songList, songListLP);

        this.setPalette(ColorPalette.current);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.name.setTextColor(palette.text);
        this.name.setBackgroundColor(palette.background);
        this.thumbnail.setBackgroundColor(palette.background);
        this.shuffleButton.setColorFilter(palette.textDisabled);
        this.shuffleButton.setBackground(new Pressable(palette.onAccent));
        this.playButton.setColorFilter(palette.primary);
        this.playButton.setBackground(new Pressable(palette.onAccent));
        this.artistName.setTextColor(palette.text);
        for (int i = 0; i < this.albumLayout.getChildCount(); i++) {
            if (!(this.getChildAt(i) instanceof TextView view)) {
                continue;
            }
            view.setTextColor(palette.textSecondary);
        }
    }

    public void setAlbum(@NonNull AlbumItem item) {
        this.shimmer.hide();

        this.name.setText(item.name);
        if (item.thumbnail != null) {
            this.activity.client.render(item.thumbnail.profile()).into(this.thumbnail);
        }
        if (item.artist != null) {
            this.artistName.setText(item.artist.name);
            if (item.artist.thumbnail != null) {
                this.activity
                        .client
                        .render(item.artist.thumbnail.profile())
                        .into(new CircularBitmapTarget(this.artistThumbnail));
            }
        }
        for (String text : new String[] {item.year, item.itemCount, item.duration}) {
            if (text != null) {
                this.addSecondaryInfo(text);
            }
        }
        this.songList.setSongs(item.songs);
    }

    @NonNull
    @CanIgnoreReturnValue
    protected TextView addSecondaryInfo(@NonNull String text) {
        View above = this.lastSecondaryText != null ? this.lastSecondaryText : this.name;
        TextView view = ensureId(new TextView(this.getContext()));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.setMargins(0, pixels(8), 0, 0);
        params.addRule(RelativeLayout.BELOW, above.getId());
        params.addRule(RelativeLayout.ALIGN_START, above.getId());
        params.addRule(RelativeLayout.ALIGN_END, above.getId());
        view.setTextSize(16);
        view.setTypeface(Typeface.DEFAULT_BOLD);
        view.setTextColor(ColorPalette.current.textSecondary);
        view.setText(text);
        this.albumLayout.addView(view, params);
        this.lastSecondaryText = view;
        return view;
    }

    @NonNull
    private RelativeLayout createAlbum(@NonNull Context context) {
        RelativeLayout layout = new RelativeLayout(context);

        int m = pixels(PADDING);

        RelativeLayout.LayoutParams thumbnailLP =
                new RelativeLayout.LayoutParams(pixels(THUMBNAIL), pixels(THUMBNAIL));
        thumbnailLP.topMargin = m;
        thumbnailLP.addRule(RelativeLayout.ALIGN_PARENT_START);
        this.thumbnail.setShapeAppearanceModel(
                new ShapeAppearanceModel.Builder()
                        .setAllCornerSizes(new RelativeCornerSize(0.15f))
                        .build());

        RelativeLayout.LayoutParams nameLP = new RelativeLayout.LayoutParams(-2, -2);
        nameLP.setMargins(m, m, m, 0);
        nameLP.addRule(RelativeLayout.END_OF, this.thumbnail.getId());
        nameLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        this.name.setTextSize(32);
        this.name.setTypeface(Typeface.DEFAULT_BOLD);
        this.name.setSingleLine();
        this.name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.name.setHorizontallyScrolling(true);
        this.name.setSelected(true);

        layout.addView(this.thumbnail, thumbnailLP);
        layout.addView(this.name, nameLP);
        return layout;
    }

    @NonNull
    private RelativeLayout createPlayer(@NonNull Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        Resources.Theme theme = this.getContext().getTheme();

        RelativeLayout.LayoutParams playLP =
                new RelativeLayout.LayoutParams(
                        pixels(ICON + PADDING * 2), pixels(ICON + PADDING * 2));
        playLP.setMarginEnd(pixels(PADDING));
        playLP.addRule(RelativeLayout.CENTER_VERTICAL);
        playLP.addRule(RelativeLayout.ALIGN_PARENT_END);
        this.playButton.setImageDrawable(theme.getDrawable(R.drawable.play_circle));
        this.playButton.setClickable(true);

        RelativeLayout.LayoutParams shuffleLP =
                new RelativeLayout.LayoutParams(pixels(ICON), pixels(ICON));
        shuffleLP.setMarginEnd(pixels(PADDING));
        shuffleLP.addRule(RelativeLayout.CENTER_VERTICAL);
        shuffleLP.addRule(RelativeLayout.START_OF, this.playButton.getId());
        this.shuffleButton.setImageDrawable(theme.getDrawable(R.drawable.shuffle));
        this.shuffleButton.setClickable(true);

        layout.addView(this.playButton, playLP);
        layout.addView(this.shuffleButton, shuffleLP);
        return layout;
    }

    @NonNull
    private RelativeLayout createArtist(@NonNull Context context) {
        RelativeLayout layout = new RelativeLayout(context);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        RelativeLayout.LayoutParams nameLP = new RelativeLayout.LayoutParams(-2, -2);
        nameLP.addRule(RelativeLayout.CENTER_HORIZONTAL);
        nameLP.addRule(RelativeLayout.END_OF, this.artistThumbnail.getId());
        nameLP.setMarginStart(pixels(PADDING / 2));
        this.artistName.setTextSize(24);
        this.artistName.setTypeface(Typeface.DEFAULT_BOLD);
        this.artistName.setSingleLine();
        this.artistName.setEllipsize(TextUtils.TruncateAt.END);

        RelativeLayout.LayoutParams thumbnailLP =
                new RelativeLayout.LayoutParams(pixels(PADDING * 2), pixels(PADDING * 2));
        thumbnailLP.addRule(RelativeLayout.ALIGN_TOP, this.artistName.getId());
        thumbnailLP.addRule(RelativeLayout.ALIGN_BOTTOM, this.artistName.getId());

        layout.addView(this.artistName, nameLP);
        layout.addView(this.artistThumbnail, thumbnailLP);
        return layout;
    }
}
