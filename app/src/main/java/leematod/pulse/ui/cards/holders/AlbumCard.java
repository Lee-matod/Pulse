package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import leematod.pulse.MainActivity;
import leematod.pulse.models.Item;
import leematod.pulse.ui.layouts.album.AlbumLayout;

public class AlbumCard extends ItemCard {
    public AlbumCard(@NonNull Context context) {
        super(context);
        this.subtitle.setBackgroundColor(Color.TRANSPARENT);
    }

    @Override
    public void onClicked(@NonNull View view, @NonNull Item<?> item) {
        String key = item.getKey();
        if (key == null) {
            return;
        }
        MainActivity activity = (MainActivity) this.getContext();
        AlbumLayout layout = new AlbumLayout(activity);
        activity.setContentView(layout);
        layout.setAlbum(key);
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
