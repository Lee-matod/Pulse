package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;

public class AlbumCard extends ItemCard {
    public AlbumCard(@NonNull Context context) {
        super(context);
        this.subtitle.setBackgroundColor(Color.TRANSPARENT);
    }

    @NonNull
    @Override
    public AlbumCard small() {
        return this;
    }

    @NonNull
    @Override
    public LayoutParams nameParams(@NonNull LayoutParams params) {
        params.width = pixels(THUMBNAIL_DIMENSIONS);
        params.topMargin = pixels(VERTICAL_PADDING);
        params.addRule(BELOW, this.thumbnail.getId());
        params.addRule(ALIGN_START, this.thumbnail.getId());
        params.addRule(ALIGN_END, this.thumbnail.getId());
        return params;
    }

    @NonNull
    @Override
    public LayoutParams subtitleParams(@NonNull LayoutParams params) {
        params.width = pixels(THUMBNAIL_DIMENSIONS);
        params.addRule(BELOW, this.name.getId());
        params.addRule(ALIGN_START, this.thumbnail.getId());
        params.addRule(ALIGN_END, this.thumbnail.getId());
        return params;
    }
}
