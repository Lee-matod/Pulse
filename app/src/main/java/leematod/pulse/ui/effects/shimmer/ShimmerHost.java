package leematod.pulse.ui.effects.shimmer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;

public class ShimmerHost extends RelativeLayout {
    protected final @NonNull ShimmerDrawable drawable;
    public float tilt = 15f;
    public int duration = 2500;
    public int startDelay = 0;
    public int repeatDelay = 0;
    public int repeatCount = ValueAnimator.INFINITE;
    public int repeatMode = ValueAnimator.RESTART;
    public @NonNull Interpolator interpolator = new LinearInterpolator();
    protected boolean showShimmer = true;
    private boolean visibilityStop = false;

    public ShimmerHost(@NonNull View view) {
        this(view.getContext());
        this.addView(view);
    }

    public ShimmerHost(@NonNull Context context) {
        super(context);
        this.setWillNotDraw(false);

        this.drawable = new ShimmerDrawable();
        this.drawable.setShimmer(this);
        this.drawable.setCallback(this);

        this.setLayerType(LAYER_TYPE_HARDWARE, new Paint());
    }

    public void start() {
        if (!this.isAttachedToWindow()) {
            return;
        }
        this.drawable.start();
    }

    public void stop() {
        this.visibilityStop = false;
        this.drawable.stop();
    }

    public void show() {
        this.show(true);
    }

    public void show(boolean alsoStart) {
        this.showShimmer = true;
        if (alsoStart) {
            this.start();
        }
        this.invalidate();
    }

    public void hide() {
        this.stop();
        this.showShimmer = false;
        this.invalidate();
    }

    public boolean isShimmerShown() {
        return this.showShimmer;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        this.drawable.setBounds(0, 0, this.getWidth(), this.getHeight());
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        // This is also called on startup, before drawable is set
        if (this.drawable == null) {
            return;
        }
        if (visibility != VISIBLE) {
            if (this.drawable.isShimmerStarted()) {
                this.stop();
                this.visibilityStop = true;
            }
        } else if (this.visibilityStop) {
            this.start();
            this.visibilityStop = false;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.showShimmer) {
            this.start();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.drawable.isShimmerStarted()) {
            this.stop();
        }
    }

    @Override
    protected void dispatchDraw(@NonNull Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.showShimmer) {
            this.drawable.draw(canvas);
        }
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return super.verifyDrawable(who) || who == this.drawable;
    }
}
