package leematod.pulse.ui.layouts.playlist;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;

import leematod.pulse.MainActivity;
import leematod.pulse.R;
import leematod.pulse.http.innertube.InnertubeResponse;
import leematod.pulse.models.PlaylistItem;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.components.ContainerizedLayout;
import leematod.pulse.ui.components.Sidebar;
import leematod.pulse.ui.effects.Pressable;
import leematod.pulse.ui.effects.shimmer.ShimmerHost;

public class PlaylistScreen extends ContainerizedLayout implements Styleable {
    public static final int PADDING = 16;
    public static final int THUMBNAIL = 128;
    public static final int TEXT = 32;
    public static final int BUTTON = 64;
    public final @NonNull TextView name;
    public final @NonNull ShapeableImageView thumbnail;
    public final @NonNull TextView author;
    public final @NonNull TextView metadata;
    public final @NonNull ShimmerHost shimmer;
    public final @NonNull ImageView playButton;
    public final @NonNull ImageView shuffleButton;
    protected final @NonNull MainActivity activity;

    public PlaylistScreen(@NonNull Context context) {
        super(context);
        this.activity = (MainActivity) context;
        this.shimmer = ensureId(new ShimmerHost(context));
        this.name = ensureId(new TextView(context));
        this.thumbnail = ensureId(new ShapeableImageView(context));
        this.author = ensureId(new TextView(context));
        this.metadata = new TextView(context);
        this.createInfo();

        RelativeLayout.LayoutParams shimmerLP = new RelativeLayout.LayoutParams(-1, -2);
        shimmerLP.topMargin = pixels(Sidebar.WIDTH);
        this.addView(this.shimmer, shimmerLP);

        this.playButton = ensureId(new ImageView(context));
        this.shuffleButton = new ImageView(context);
        this.createPlayer();

        this.setPalette(ColorPalette.current);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.name.setBackgroundColor(palette.background);
        this.name.setTextColor(palette.text);
        this.thumbnail.setBackgroundColor(palette.background);
        this.author.setBackgroundColor(palette.background);
        this.author.setTextColor(palette.textSecondary);
        this.metadata.setBackgroundColor(palette.background);
        this.metadata.setTextColor(palette.textSecondary);

        Pressable shufflePress = new Pressable(palette.onAccent);
        shufflePress.setAnimationOrigin(Pressable.OriginType.CLICK);
        this.shuffleButton.setBackground(shufflePress);
        this.shuffleButton.setColorFilter(palette.secondary);

        Pressable playPress = new Pressable(palette.onAccent);
        playPress.setAnimationOrigin(Pressable.OriginType.CLICK);
        this.playButton.setBackground(playPress);
        this.playButton.setColorFilter(palette.primary);
    }

    public void setPlaylist(@NonNull String playlistId) {
        this.activity.client
                .playlist(playlistId)
                .thenAccept((item) -> this.post(() -> this.setPlaylist(item)));
    }

    protected void setPlaylist(@NonNull InnertubeResponse<PlaylistItem> response) {
        PlaylistItem item = response.response;
        if (item == null) {
            return;
        }
        this.shimmer.hide();
        this.name.setText(item.name);
        if (item.author != null) {
            this.author.setText(item.author.name);
        }
        if (item.itemCount != null && item.duration != null) {
            String metadata = String.join(" • ", item.itemCount, item.duration);
            this.metadata.setText(metadata);
        }
        if (item.thumbnail != null) {
            this.activity.client.render(item.thumbnail.profile()).into(this.thumbnail);
        }
    }

    private void createSongs() {

    }

    private void createInfo() {
        RelativeLayout.LayoutParams thumbnailLP =
                new RelativeLayout.LayoutParams(pixels(THUMBNAIL), pixels(THUMBNAIL));
        this.thumbnail.setShapeAppearanceModel(
                new ShapeAppearanceModel.Builder()
                        .setAllCornerSizes(new RelativeCornerSize(0.15f))
                        .build());

        this.name.setTextSize(24);
        this.name.setSingleLine();
        this.name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.name.setHorizontallyScrolling(true);
        this.name.setSelected(true);
        this.name.setTypeface(Typeface.DEFAULT_BOLD);
        this.author.setTextSize(20);
        this.author.setTypeface(Typeface.DEFAULT_BOLD);
        this.metadata.setTextSize(20);
        this.metadata.setTypeface(Typeface.DEFAULT_BOLD);
        this.metadata.setSingleLine();

        this.shimmer.addView(this.name, this.createTextParams(null));
        this.shimmer.addView(this.author, this.createTextParams(this.name.getId()));
        this.shimmer.addView(this.metadata, this.createTextParams(this.author.getId()));
        this.shimmer.addView(this.thumbnail, thumbnailLP);
    }

    private void createPlayer() {
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(pixels(BUTTON), pixels(BUTTON));
        params.topMargin = pixels(PADDING);
        params.setMarginEnd(pixels(PADDING / 2));
        params.addRule(RelativeLayout.BELOW, this.shimmer.getId());

        RelativeLayout.LayoutParams playLP = new RelativeLayout.LayoutParams(params);
        playLP.addRule(RelativeLayout.ALIGN_PARENT_END);

        RelativeLayout.LayoutParams shuffleLP = new RelativeLayout.LayoutParams(params);
        shuffleLP.addRule(RelativeLayout.START_OF, this.playButton.getId());

        this.playButton.setImageResource(R.drawable.play_circle);
        this.playButton.setClickable(true);
        this.shuffleButton.setImageResource(R.drawable.shuffle);
        this.shuffleButton.setClickable(true);

        this.layout.addView(this.playButton, playLP);
        this.layout.addView(this.shuffleButton, shuffleLP);
    }

    @NonNull
    private RelativeLayout.LayoutParams createTextParams(@Nullable Integer above) {
        int pad = pixels(PADDING);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, pixels(TEXT));
        params.topMargin = pad / 2;
        params.setMarginStart(pad);
        params.setMarginEnd(pad);
        params.addRule(RelativeLayout.END_OF, this.thumbnail.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_END);

        if (above != null) {
            params.addRule(RelativeLayout.BELOW, above);
        }
        return params;
    }
}
