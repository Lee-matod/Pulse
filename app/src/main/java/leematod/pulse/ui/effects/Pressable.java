package leematod.pulse.ui.effects;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Pressable extends Drawable {
    public static final int CIRCLE = 0;
    public static final int HORIZONTAL = 1;
    public static final int VERTICAL = 2;
    public static final int CENTER = 10;
    public static final int TOUCH = 11;
    private final @NonNull Paint paint;
    private final @NonNull Rect bounds;
    protected @IntRange(from = CIRCLE, to = VERTICAL) int pressType = CIRCLE;
    protected @IntRange(from = CENTER, to = TOUCH) int originType = CENTER;
    protected int borderRadius = 30;
    protected int speed = 150;
    private @Nullable ValueAnimator animator;
    private boolean isActive;
    private int touchX = 0;
    private int touchY = 0;

    public Pressable(int accent) {
        this.paint = new Paint();
        this.paint.setColor(accent);
        this.paint.setAntiAlias(true);
        this.paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        this.bounds = new Rect();
        this.setAlpha(50);
        this.setActive(false);
    }

    @Override
    protected void onBoundsChange(@NonNull Rect bounds) {
        super.onBoundsChange(bounds);
        this.bounds.set(bounds);
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    protected boolean onStateChange(@NonNull int[] states) {
        boolean changed = super.onStateChange(states);

        boolean enabled = false;
        boolean pressed = false;

        for (int state : states) {
            if (state == android.R.attr.state_enabled) {
                enabled = true;
            } else if (state == android.R.attr.state_pressed) {
                pressed = true;
            }
        }
        this.setActive(enabled && pressed);
        return changed;
    }

    @Override
    public void setHotspot(float x, float y) {
        super.setHotspot(x, y);
        this.touchX = (int) x;
        this.touchY = (int) y;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        if (this.animator != null) {
            float value = (float) this.animator.getAnimatedValue();
            int width = this.bounds.width();
            int height = this.bounds.height();

            int originX = this.originType == TOUCH ? this.touchX : this.bounds.centerX();
            int originY = this.originType == TOUCH ? this.touchY : this.bounds.centerY();

            float left = Math.max(0, originX - value * width);
            float top = Math.max(0, originY - value * height);
            float right = Math.min(width, originX + value * width);
            float bottom = Math.min(height, originY + value * height);

            RectF rectF =
                    switch (this.pressType) {
                        case HORIZONTAL -> new RectF(left, 0, right, this.bounds.bottom);
                        case VERTICAL -> new RectF(0, top, this.bounds.left, bottom);
                        default -> new RectF(left, top, right, bottom);
                    };

            canvas.drawRoundRect(rectF, this.borderRadius, this.borderRadius, this.paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        this.paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
    }

    public void setPressType(@IntRange(from = CIRCLE, to = VERTICAL) int value) {
        this.pressType = value;
    }

    public void setOriginType(@IntRange(from = CENTER, to = TOUCH) int value) {
        this.originType = value;
    }

    public void setActive(boolean active) {
        if (this.isActive == active) {
            return;
        }
        this.isActive = active;
        float start = active ? 0 : 1;
        float end = 1f - start;

        this.setAnimator(start, end);
    }

    private void setAnimator(float start, float end) {
        if (this.animator != null) {
            this.animator.cancel();
            this.animator.removeAllUpdateListeners();
        }
        this.animator = ValueAnimator.ofFloat(start, end);
        this.animator.setDuration(this.speed);
        this.animator.addUpdateListener((a) -> this.invalidateSelf());
        this.animator.start();
    }
}
