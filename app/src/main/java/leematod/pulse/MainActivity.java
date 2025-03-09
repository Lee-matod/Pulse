package leematod.pulse;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import leematod.pulse.http.HTTPClient;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.layouts.home.HomeLayout;

public class MainActivity extends Activity {
    public HTTPClient client;
    public View contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.resources = this.getResources();
        this.client = new HTTPClient(this);

        this.setContentView(new HomeLayout(this));
    }

    public void setStyle() {
        this.setStyle(ColorPalette.current);
    }

    public void setStyle(@NonNull ColorPalette palette) {
        this.refreshViewStyle(palette, this.contentView);
    }

    private void refreshViewStyle(@NonNull ColorPalette palette, @NonNull View view) {
        if (view instanceof Styleable styleable) {
            styleable.setPalette(palette);
        }

        if (view instanceof ViewGroup group) {
            for (int i = 0; i < group.getChildCount(); i++) {
                this.refreshViewStyle(palette, group.getChildAt(i));
            }
        }
    }

    @Override
    public void setContentView(@NonNull View view) {
        super.setContentView(view);
        this.contentView = view;
    }
}
