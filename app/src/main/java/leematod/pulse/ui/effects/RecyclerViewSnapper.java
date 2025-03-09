package leematod.pulse.ui.effects;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewSnapper extends RecyclerView.OnScrollListener {
    protected final @NonNull RecyclerView recyclerView;
    private boolean locked;

    public RecyclerViewSnapper(@NonNull RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.locked = true;
    }

    @Override
    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        RecyclerView.LayoutManager maybeLayoutManager = this.recyclerView.getLayoutManager();
        if (!(maybeLayoutManager instanceof LinearLayoutManager layoutManager)) {
            return;
        }

        switch (newState) {
            case RecyclerView.SCROLL_STATE_DRAGGING:
                this.locked = false;
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
            case RecyclerView.SCROLL_STATE_IDLE:
                if (this.locked) {
                    return;
                }
                int firstPosition = layoutManager.findFirstVisibleItemPosition();
                int lastPosition = layoutManager.findLastVisibleItemPosition();
                View firstView = layoutManager.findViewByPosition(firstPosition);
                View lastView = layoutManager.findViewByPosition(lastPosition);
                if (firstView == null || lastView == null) {
                    return;
                }
                this.locked = true;

                View targetView =
                        this.getVisiblePortion(lastView) > this.getVisiblePortion(firstView)
                                ? lastView
                                : firstView;

                this.recyclerView.smoothScrollBy(
                        (int) targetView.getX(), 0);
        }
    }

    private float getVisiblePortion(@NonNull View view) {
        float start = Math.max(0, view.getX());
        float end = Math.min(this.recyclerView.getWidth(), view.getX() + view.getWidth());
        if (end > start) {
            return end - start;
        }
        return 0;
    }
}
