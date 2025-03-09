package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;
import com.google.errorprone.annotations.CanIgnoreReturnValue;

import leematod.pulse.ui.components.PrimaryTextView;
import leematod.pulse.ui.components.SecondaryTextView;

public abstract class ItemCard extends RelativeLayout {
    public static final int VERTICAL_PADDING = 8;
    public static final int HORIZONTAL_PADDING = 8;
    public static final int THUMBNAIL_DIMENSIONS = 116;
    public static final int NAME_HEIGHT = 24;
    public static final int SUBTITLE_HEIGHT = 20;
    public static final int TOTAL_HEIGHT = THUMBNAIL_DIMENSIONS + NAME_HEIGHT + SUBTITLE_HEIGHT;

    public final @NonNull TextView name;
    public final @NonNull TextView subtitle;
    public final @NonNull ShapeableImageView thumbnail;

    public ItemCard(@NonNull Context context) {
        super(context);

        this.setLayoutParams(this.layoutParams(new LayoutParams(-2, -2)));

        this.name = ensureId(new PrimaryTextView(context));
        this.subtitle = ensureId(new SecondaryTextView(context));
        this.thumbnail = ensureId(new ShapeableImageView(context));
        // It doesn't really matter what color we use, because we just need it for the shimmer
        this.thumbnail.setBackgroundColor(Color.BLACK);
        this.thumbnail.setShapeAppearanceModel(
                new ShapeAppearanceModel.Builder()
                        .setAllCornerSizes(new RelativeCornerSize(0.15f))
                        .build());

        this.addView(this.name, this.nameParams(new LayoutParams(-2, pixels(NAME_HEIGHT))));
        this.addView(
                this.subtitle, this.subtitleParams(new LayoutParams(-2, pixels(SUBTITLE_HEIGHT))));
        this.addView(
                this.thumbnail,
                this.thumbnailParams(
                        new LayoutParams(
                                pixels(THUMBNAIL_DIMENSIONS), pixels(THUMBNAIL_DIMENSIONS))));

        this.setOnClickListener(this::onClicked);
    }

    protected void onClicked(@NonNull View view) {}

    // TODO: actual implementations in subclasses
    @NonNull
    @CanIgnoreReturnValue
    public abstract ItemCard small();

    @NonNull
    public LayoutParams layoutParams(@NonNull LayoutParams params) {
        params.setMarginEnd(pixels(HORIZONTAL_PADDING));
        return params;
    }

    @NonNull
    public LayoutParams nameParams(@NonNull LayoutParams params) {
        return params;
    }

    @NonNull
    public LayoutParams subtitleParams(@NonNull LayoutParams params) {
        return params;
    }

    @NonNull
    public LayoutParams thumbnailParams(@NonNull LayoutParams params) {
        return params;
    }
}
