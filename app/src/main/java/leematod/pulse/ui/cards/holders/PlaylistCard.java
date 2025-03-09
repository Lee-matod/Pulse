package leematod.pulse.ui.cards.holders;

import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

import leematod.pulse.R;

public class PlaylistCard extends AlbumCard {

    public PlaylistCard(@NonNull Context context) {
        super(context);
        this.subtitle.setSingleLine(false);
        this.subtitle.setMaxLines(2);

        GradientDrawable gradient = new GradientDrawable();
        gradient.setColors(new int[] {Color.TRANSPARENT, Color.BLACK});
        gradient.setOrientation(GradientDrawable.Orientation.LEFT_RIGHT);

        RelativeLayout effectLayer = new RelativeLayout(context);
        effectLayer.setBackground(gradient);
        this.addView(
                effectLayer,
                new LayoutParams(pixels(THUMBNAIL_DIMENSIONS), pixels(THUMBNAIL_DIMENSIONS)));

        LayoutParams playlistLP = new LayoutParams(pixels(32), pixels(32));
        playlistLP.addRule(ALIGN_PARENT_END);
        playlistLP.addRule(CENTER_VERTICAL);
        playlistLP.setMarginEnd(pixels(20));

        View playlistView = new View(context);
        playlistView.setBackground(context.getTheme().getDrawable(R.drawable.playlist));
        effectLayer.addView(playlistView, playlistLP);
    }

    @NonNull
    @Override
    public PlaylistCard small() {
        return this;
    }

    @NonNull
    @Override
    public LayoutParams subtitleParams(@NonNull LayoutParams params) {
        params.height = -2;
        return super.subtitleParams(params);
    }
}
