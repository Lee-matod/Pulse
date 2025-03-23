package leematod.pulse.ui.effects;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso3.BitmapTarget;
import com.squareup.picasso3.Picasso;

import java.util.function.Consumer;

public class CircularBitmapTarget implements BitmapTarget {
    private final @Nullable ImageView target;
    private final @Nullable Consumer<Bitmap> callback;

    public CircularBitmapTarget(@NonNull ImageView target) {
        this.target = target;
        this.callback = null;
    }

    public CircularBitmapTarget(@NonNull Consumer<Bitmap> callback) {
        this.target = null;
        this.callback = callback;
    }

    @Override
    public void onBitmapLoaded(@NonNull Bitmap bitmap, @NonNull Picasso.LoadedFrom loadedFrom) {
        Bitmap circular =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        BitmapShader shader =
                new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setShader(shader);
        paint.setAntiAlias(true);
        Canvas canvas = new Canvas(circular);
        canvas.drawCircle(
                (float) bitmap.getWidth() / 2,
                (float) bitmap.getHeight() / 2,
                (float) bitmap.getWidth() / 2,
                paint);
        if (this.target != null) {
            this.target.setImageBitmap(circular);
        }
        if (this.callback != null) {
            this.callback.accept(circular);
        }
    }

    @Override
    public void onBitmapFailed(@NonNull Exception e, @Nullable Drawable drawable) {}

    @Override
    public void onPrepareLoad(@Nullable Drawable drawable) {}
}
