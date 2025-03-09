package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.view.Gravity;

import androidx.annotation.NonNull;

public class SongCard extends ItemCard {
    public static final int WIDTH = 300;
    public static final int HEIGHT = TOTAL_HEIGHT / 2 - VERTICAL_PADDING - HORIZONTAL_PADDING;

    public SongCard(@NonNull Context context) {
        super(context);
        this.name.setGravity(Gravity.CENTER_VERTICAL);
        this.name.setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
        this.subtitle.setGravity(Gravity.TOP);
        this.subtitle.setTextAlignment(TEXT_ALIGNMENT_GRAVITY);
    }

    @NonNull
    public SongCard small() {
        return this;
    }

    @NonNull
    @Override
    public LayoutParams layoutParams(@NonNull LayoutParams params) {
        params.width = pixels(WIDTH);
        params.height = pixels(HEIGHT);
        params.bottomMargin = pixels(HORIZONTAL_PADDING);
        return params;
    }

    @NonNull
    @Override
    public LayoutParams nameParams(@NonNull LayoutParams params) {
        params.width = pixels(WIDTH - THUMBNAIL_DIMENSIONS);
        params.topMargin = pixels(VERTICAL_PADDING);
        params.setMarginStart(pixels(HORIZONTAL_PADDING));
        params.addRule(RIGHT_OF, this.thumbnail.getId());
        params.addRule(ALIGN_TOP, this.thumbnail.getId());
        return params;
    }

    @NonNull
    @Override
    public LayoutParams subtitleParams(@NonNull LayoutParams params) {
        params.width = pixels(WIDTH - THUMBNAIL_DIMENSIONS);
        params.topMargin = pixels(VERTICAL_PADDING);
        params.setMarginStart(pixels(HORIZONTAL_PADDING));
        params.addRule(RIGHT_OF, this.thumbnail.getId());
        params.addRule(BELOW, this.name.getId());
        return params;
    }

    @NonNull
    @Override
    public LayoutParams thumbnailParams(@NonNull LayoutParams params) {
        params.width = pixels(HEIGHT);
        params.addRule(ALIGN_PARENT_TOP);
        params.addRule(ALIGN_PARENT_BOTTOM);
        return params;
    }
}
