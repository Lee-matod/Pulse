package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;

import leematod.pulse.MainActivity;
import leematod.pulse.models.Item;
import leematod.pulse.ui.layouts.artist.ArtistLayout;

public class ArtistCard extends ItemCard {
    public ArtistCard(@NonNull Context context) {
        super(context);
        this.removeView(this.subtitle);

        this.thumbnail.setShapeAppearanceModel(
                new ShapeAppearanceModel.Builder()
                        .setAllCornerSizes(new RelativeCornerSize(0.5f))
                        .build());
    }

    @Override
    public void onClicked(@NonNull View view, @NonNull Item<?> item) {
        String key = item.getKey();
        if (key == null) {
            return;
        }
        MainActivity activity = (MainActivity) this.getContext();
        ArtistLayout layout = new ArtistLayout(activity);
        activity.setContentView(layout);
        layout.setArtist(key);
    }

    @NonNull
    @Override
    public ArtistCard small() {
        return this;
    }

    @NonNull
    @Override
    public LayoutParams nameParams(@NonNull LayoutParams params) {
        params.topMargin = pixels(VERTICAL_PADDING);
        params.addRule(BELOW, this.thumbnail.getId());
        params.addRule(ALIGN_START, this.thumbnail.getId());
        params.addRule(ALIGN_END, this.thumbnail.getId());
        return params;
    }
}
