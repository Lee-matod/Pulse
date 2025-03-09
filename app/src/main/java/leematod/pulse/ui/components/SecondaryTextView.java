package leematod.pulse.ui.components;

import android.content.Context;

import androidx.annotation.NonNull;

import leematod.pulse.ui.ColorPalette;

public class SecondaryTextView extends PrimaryTextView {
    public SecondaryTextView(Context context) {
        super(context);
        this.setTextSize(14);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.setTextColor(palette.textSecondary);
        this.setBackgroundColor(palette.background);
    }
}
