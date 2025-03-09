package leematod.pulse.ui.effects.shimmer;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.ui.ColorPalette;

public class ShimmerDrawable extends Drawable {
    protected final int[] colors = new int[3];
    private final @NonNull Paint paint;
    private final @NonNull Matrix matrix;
    private final @NonNull Rect bounds;
    protected @Nullable ValueAnimator animator;
    protected @Nullable ShimmerHost shimmer;

    public ShimmerDrawable() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        this.matrix = new Matrix();
        this.bounds = new Rect();
        this.animator = null;

        this.setShimmer(null);
    }

    public void setShimmer(@Nullable ShimmerHost shimmer) {
        this.shimmer = shimmer;
        this.setShader();
        this.setAnimator();
        this.invalidateSelf();
    }

    public void start() {
        if (this.animator != null && !this.isShimmerStarted()) {
            this.updateAnimatorValues();
            this.animator.start();
        }
    }

    public void stop() {
        if (this.animator != null && this.isShimmerStarted()) {
            this.animator.cancel();
        }
    }

    public boolean isShimmerStarted() {
        return this.animator != null && this.animator.isStarted();
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds.set(bounds);
        this.setShader();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (this.shimmer == null || this.paint.getShader() == null) {
            return;
        }

        int width = this.bounds.width();
        int height = this.bounds.height();

        float tiltTan = (float) Math.tan(Math.toRadians(this.shimmer.tilt));
        float translateWidth = width + tiltTan * height;
        float animatedValue = this.animator != null ? (float) this.animator.getAnimatedValue() : 0f;
        float offset = translateWidth * (2 * animatedValue - 1);

        this.matrix.reset();
        this.matrix.setRotate(this.shimmer.tilt, width / 2f, height / 2f);
        this.matrix.preTranslate(offset * 3, 0);
        this.paint.getShader().setLocalMatrix(this.matrix);

        canvas.drawRect(this.bounds, this.paint);
    }

    @Override
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    protected void updateAnimatorValues() {
        if (this.animator == null) {
            return;
        }
        this.colors[0] = ColorPalette.current.accent;
        this.colors[1] = ColorPalette.current.onAccent;
        this.colors[2] = ColorPalette.current.accent;
        if (this.shimmer != null) {
            this.animator.setInterpolator(this.shimmer.interpolator);
            this.animator.setRepeatMode(this.shimmer.repeatMode);
            this.animator.setStartDelay(this.shimmer.startDelay);
            this.animator.setRepeatCount(this.shimmer.repeatCount);
            this.animator.setDuration(this.shimmer.duration + this.shimmer.repeatDelay);
        } else {
            this.animator.setInterpolator(null);
            this.animator.setRepeatMode(ValueAnimator.RESTART);
            this.animator.setStartDelay(0);
            this.animator.setRepeatCount(ValueAnimator.INFINITE);
            this.animator.setDuration(300);
        }
    }

    protected void setAnimator() {
        boolean started;
        if (this.animator != null) {
            started = this.animator.isStarted();
            this.animator.cancel();
            this.animator.removeAllUpdateListeners();
        } else {
            started = false;
        }

        float upperBound =
                this.shimmer != null
                        ? (float) this.shimmer.repeatDelay / this.shimmer.duration
                        : 0f;
        this.animator = ValueAnimator.ofFloat(0f, 1f + upperBound);
        this.animator.addUpdateListener((a) -> this.invalidateSelf());
        this.updateAnimatorValues();

        if (started) {
            this.animator.start();
        }
    }

    protected void setShader() {
        if (this.shimmer == null) {
            return;
        }

        Rect bounds = this.getBounds();
        int width = bounds.width();
        int height = bounds.height();

        if (width == 0 || height == 0) {
            return;
        }
        this.paint.setShader(
                new LinearGradient(0, 0, width, 0, this.colors, null, Shader.TileMode.CLAMP));
    }
}
