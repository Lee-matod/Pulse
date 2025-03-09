package leematod.pulse.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;

import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;

@SuppressLint("AppCompatCustomView")
public class PrimaryTextView extends TextView implements Styleable {
    public PrimaryTextView(Context context) {
        super(context);
        this.setTextSize(16);
        this.setSingleLine();
        this.setEllipsize(TextUtils.TruncateAt.END);
        this.setTypeface(Typeface.DEFAULT_BOLD);
        this.setPalette(ColorPalette.current);
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        this.setTextColor(palette.text);
        this.setBackgroundColor(palette.background);
    }
}
