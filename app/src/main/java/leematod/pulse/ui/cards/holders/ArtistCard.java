package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.shape.RelativeCornerSize;
import com.google.android.material.shape.ShapeAppearanceModel;

public class ArtistCard extends ItemCard {
    public ArtistCard(@NonNull Context context) {
        super(context);
        this.removeView(this.subtitle);

        this.thumbnail.setShapeAppearanceModel(
                new ShapeAppearanceModel.Builder()
                        .setAllCornerSizes(new RelativeCornerSize(0.5f))
                        .build());
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
