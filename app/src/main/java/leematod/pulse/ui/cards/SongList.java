package leematod.pulse.ui.cards;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import leematod.pulse.models.SongItem;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.DynamicRecyclerAdapter;
import leematod.pulse.ui.cards.holders.SongCard;

import java.util.ArrayList;
import java.util.List;

public class SongList extends RecyclerView {
    protected final @NonNull List<SongItem> songs;
    protected final @NonNull SongListAdapter adapter;
    private boolean enumeratedList = false;

    public SongList(@NonNull Context context) {
        super(context);
        this.songs = new ArrayList<>();
        this.adapter = new SongListAdapter();
        this.setAdapter(this.adapter);
        this.setLayoutManager(new LinearLayoutManager(context));

        this.setDummyCount(5);
    }

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        if (this.getAdapter() == null) {
            super.setAdapter(adapter);
        }
    }

    public void setDummyCount(int count) {
        this.adapter.setStartupDummies(count);
    }

    public void enumerate() {
        this.enumerate(true);
    }

    public void enumerate(boolean enabled) {
        this.enumeratedList = enabled;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSongs(@NonNull List<SongItem> songs) {
        this.songs.clear();
        this.songs.addAll(songs);
        this.adapter.notifyDataSetChanged();
    }

    protected class SongListAdapter extends DynamicRecyclerAdapter<SongItem, RelativeLayout> {

        @Override
        public int getRealItemCount() {
            return SongList.this.songs.size();
        }

        @NonNull
        @Override
        public SongItem getItemAt(int position) {
            return SongList.this.songs.get(position);
        }

        @NonNull
        @Override
        public SongItem getDummyAt(int position) {
            return new SongItem("Song title field", null, null, null);
        }

        @Override
        public void bindView(
                @NonNull SongItem item,
                @NonNull RelativeLayout view,
                int position,
                boolean isDummy) {
            view.removeAllViews();
            Context context = view.getContext();
            SongCard card = ensureId(new SongCard(context));
            TextView duration = ensureId(new TextView(context));

            RelativeLayout.LayoutParams cardLP =
                    new RelativeLayout.LayoutParams(-2, pixels(SongCard.HEIGHT));
            cardLP.bottomMargin = pixels(16);
            cardLP.setMarginEnd(pixels(16));
            cardLP.addRule(RelativeLayout.START_OF, duration.getId());
            RelativeLayout.LayoutParams cardNameLP =
                    (RelativeLayout.LayoutParams) card.name.getLayoutParams();
            cardNameLP.width = -2;
            card.name.setText(item.name);
            card.name.setTextSize(20);
            card.subtitle.setText(item.metadata);
            card.removeView(card.thumbnail);

            RelativeLayout.LayoutParams durationLP = new RelativeLayout.LayoutParams(-2, -2);
            durationLP.setMarginEnd(pixels(16));
            durationLP.addRule(RelativeLayout.ALIGN_TOP, card.getId());
            durationLP.addRule(RelativeLayout.ALIGN_BOTTOM, card.getId());
            durationLP.addRule(RelativeLayout.ALIGN_PARENT_END);
            duration.setText(item.duration);
            duration.setTypeface(Typeface.DEFAULT_BOLD);
            duration.setGravity(Gravity.CENTER);
            duration.setTextSize(16);
            duration.setTextColor(ColorPalette.current.textDisabled);

            if (SongList.this.enumeratedList) {
                int len;
                String index;

                if (item.index != null) {
                    SongItem lastItem = this.getItemAt(this.getRealItemCount() - 1);
                    if (lastItem.index != null) {
                        len = lastItem.index.length();
                    } else {
                        len = Math.max((int) Math.log10(SongList.this.songs.size()) + 1, 1);
                    }
                    index = item.index + ".";
                } else {
                    len = Math.max((int) Math.log10(SongList.this.songs.size()) + 1, 1);
                    index = position + 1 + ".";
                }

                RelativeLayout.LayoutParams indexLP =
                        new RelativeLayout.LayoutParams(pixels(20 * len + 4), -2);
                indexLP.setMarginEnd(pixels(8));
                indexLP.addRule(RelativeLayout.ALIGN_TOP, card.getId());
                indexLP.addRule(RelativeLayout.ALIGN_BOTTOM, card.getId());
                indexLP.addRule(RelativeLayout.ALIGN_PARENT_START);
                TextView indexView = ensureId(new TextView(context));
                indexView.setTextSize(24);
                indexView.setText(index);
                indexView.setGravity(Gravity.CENTER_VERTICAL);
                indexView.setTextAlignment(TEXT_ALIGNMENT_VIEW_END);
                indexView.setTypeface(Typeface.DEFAULT_BOLD);
                indexView.setTextColor(ColorPalette.current.textDisabled);
                cardLP.addRule(RelativeLayout.END_OF, indexView.getId());

                view.addView(indexView, indexLP);
            } else {
                cardLP.addRule(RelativeLayout.ALIGN_PARENT_START);
            }
            view.addView(card, cardLP);
            view.addView(duration, durationLP);
        }

        @NonNull
        @Override
        public DynamicViewHolder<RelativeLayout> onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            return new DynamicViewHolder<>(new RelativeLayout(parent.getContext()));
        }
    }
}
