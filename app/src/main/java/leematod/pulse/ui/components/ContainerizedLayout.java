package leematod.pulse.ui.components;

import static leematod.pulse.Utils.ensureId;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;

public class ContainerizedLayout extends NestedScrollView {
    protected final @NonNull RelativeLayout layout;

    public ContainerizedLayout(@NonNull Context context) {
        super(context);
        this.layout = new RelativeLayout(context);
        super.addView(this.layout);
    }

    @Override
    public void addView(@NonNull View child) {
        this.addView(child, this.layout.getChildCount());
    }

    @Override
    public void addView(View child, int index) {
        if (this.getChildCount() == 0) {
            super.addView(child, index);
        } else {
            this.insertViewAt(child, index);
        }
    }

    @Override
    public void addView(View child, int width, int height) {
        this.addView(child, new RelativeLayout.LayoutParams(width, height));
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        this.addView(child, this.layout.getChildCount(), params);
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        if (this.getChildCount() == 0) {
            super.addView(child, index, params);
        } else {
            child.setLayoutParams(params);
            this.insertViewAt(child, index);
        }
    }

    @Override
    public void removeView(@NonNull View view) {
        this.layout.removeView(view);
    }

    @Override
    public void removeViewAt(int index) {
        this.layout.removeViewAt(index);
    }

    private void insertViewAt(@NonNull View view, int index) {
        if (index < 0) {
            return;
        }

        View top;
        View bottom;
        if (index < this.layout.getChildCount()) {
            top = view;
            bottom = this.layout.getChildAt(index);
        } else {
            top = this.layout.getChildAt(this.layout.getChildCount() - 1);
            bottom = view;
            if (top == null) {
                this.layout.addView(view, index);
                return;
            }
        }
        ensureId(top);
        RelativeLayout.LayoutParams layoutParams =
                (RelativeLayout.LayoutParams) bottom.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new RelativeLayout.LayoutParams(-2, -2);
        }
        layoutParams.addRule(RelativeLayout.BELOW, top.getId());
        bottom.setLayoutParams(layoutParams);
        this.layout.addView(view, index);
    }
}
