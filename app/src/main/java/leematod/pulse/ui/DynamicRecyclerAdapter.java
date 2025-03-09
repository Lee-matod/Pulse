package leematod.pulse.ui;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import leematod.pulse.ui.effects.shimmer.ShimmerHost;

public abstract class DynamicRecyclerAdapter<T, V extends View>
        extends RecyclerView.Adapter<DynamicRecyclerAdapter.DynamicViewHolder<V>> {
    private int startupDummies = 0;
    private boolean loadMoreContent = false;

    public abstract int getRealItemCount();

    @NonNull
    public abstract T getItemAt(int position);

    @NonNull
    public abstract T getDummyAt(int position);

    public abstract void bindView(@NonNull T item, @NonNull V view, int position, boolean isDummy);

    public void willLoadMoreContent(boolean loadMore) {
        this.loadMoreContent = loadMore;
    }

    public void setStartupDummies(int count) {
        this.startupDummies = count;
    }

    @Override
    public final int getItemCount() {
        int realSize = this.getRealItemCount();
        if (realSize == 0) {
            return this.startupDummies;
        }
        return realSize + (this.loadMoreContent ? 1 : 0);
    }

    @Override
    public final void onBindViewHolder(@NonNull DynamicViewHolder<V> holder, int position) {
        T item;
        try {
            item = this.getItemAt(position);
            holder.shimmer.hide();
        } catch (IndexOutOfBoundsException exc) {
            item = this.getDummyAt(position);
            holder.shimmer.show();
        }
        this.bindView(item, holder.view, position, holder.shimmer.isShimmerShown());
    }

    public static class DynamicViewHolder<V extends View> extends RecyclerView.ViewHolder {
        public final @NonNull V view;
        public final @NonNull ShimmerHost shimmer;

        public DynamicViewHolder(@NonNull V view) {
            super(new ShimmerHost(view));
            this.view = view;
            this.shimmer = (ShimmerHost) this.itemView;
        }
    }
}
