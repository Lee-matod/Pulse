package leematod.pulse.ui.cards;

import static leematod.pulse.Utils.ensureId;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import leematod.pulse.MainActivity;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.models.ArtistItem;
import leematod.pulse.models.Item;
import leematod.pulse.models.ItemGroup;
import leematod.pulse.models.PlaylistItem;
import leematod.pulse.models.SongItem;
import leematod.pulse.ui.cards.holders.AlbumCard;
import leematod.pulse.ui.cards.holders.ArtistCard;
import leematod.pulse.ui.cards.holders.ItemCard;
import leematod.pulse.ui.cards.holders.PlaylistCard;
import leematod.pulse.ui.cards.holders.SongCard;
import leematod.pulse.ui.effects.RecyclerViewSnapper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class CardDisplay extends RecyclerView {
    protected final @NonNull List<Item<?>> items;
    protected final @NonNull List<List<Item<?>>> groupedItems;
    protected final @NonNull CardAdapter adapter;
    private final @NonNull RecyclerViewSnapper snapper;
    private final @NonNull Function<Boolean, OnFlingListener> fling =
            (enabled) ->
                    new OnFlingListener() {
                        @Override
                        public boolean onFling(int velocityX, int velocityY) {
                            if (enabled) {
                                CardDisplay.this.snapper.onScrollStateChanged(
                                        CardDisplay.this, SCROLL_STATE_SETTLING);
                            }
                            return enabled;
                        }
                    };
    private int songRowCount = 4;
    private boolean isUnique;

    public CardDisplay(@NonNull Context context) {
        super(context);
        this.items = new ArrayList<>();
        this.groupedItems = new ArrayList<>();
        this.adapter = new CardAdapter();
        this.snapper = new RecyclerViewSnapper(this);
        this.setLayoutManager(new LinearLayoutManager(context, HORIZONTAL, false));
        this.setAdapter(this.adapter);
    }

    public void setSongRowCount(int count) {
        this.songRowCount = count;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(@NonNull ItemGroup group) {
        this.items.clear();
        this.items.addAll(group.items);
        Item.Type groupType = group.getItemType();
        this.isUnique = groupType != null;

        List<Item<?>> subGroup = new ArrayList<>();
        for (Item<?> item : this.items) {
            if (item instanceof SongItem) {
                subGroup.add(item);
                if (this.isUnique) {
                    // We don't have to worry about previously inserted items, because we know the
                    // entire list is unique (only songs in this case)
                    if (subGroup.size() == this.songRowCount) {
                        this.groupedItems.add(List.copyOf(subGroup));
                        subGroup.clear();
                    }
                } else {
                    if (subGroup.size() == 2) {
                        this.groupedItems.add(List.copyOf(subGroup));
                        subGroup.clear();
                    }
                }
                continue;
            }
            List<Item<?>> tempList = new ArrayList<>();
            tempList.add(item);
            this.groupedItems.add(tempList);
        }

        if (this.isUnique && groupType == Item.Type.SONG) {
            this.addOnScrollListener(this.snapper);
            this.setOnFlingListener(this.fling.apply(true));
        } else {
            this.removeOnScrollListener(this.snapper);
            this.setOnFlingListener(this.fling.apply(false));
        }

        this.adapter.notifyDataSetChanged();
    }

    protected class CardAdapter extends RecyclerView.Adapter<ViewHolder> {
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            List<Item<?>> items = CardDisplay.this.groupedItems.get(position);

            RelativeLayout view = (RelativeLayout) holder.itemView;
            view.removeAllViews();

            ItemCard lastCard = null;
            for (Item<?> item : items) {
                Function<Context, ItemCard> cardConstructor = this.typeToCard(item);
                ItemCard card = ensureId(cardConstructor.apply(view.getContext()));

                if (!CardDisplay.this.isUnique) {
                    card.small();
                }

                if (lastCard != null) {
                    RelativeLayout.LayoutParams params =
                            (RelativeLayout.LayoutParams) card.getLayoutParams();
                    params.addRule(RelativeLayout.BELOW, lastCard.getId());
                }
                // Make sure we are not dealing with any dummies that may have been provided from
                // other dynamic recyclers
                if (item.thumbnail != null) {
                    this.setCardData(card, item);
                }
                view.addView(card);
                lastCard = card;
            }
        }

        @Override
        public int getItemCount() {
            return CardDisplay.this.groupedItems.size();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(new RelativeLayout(parent.getContext())) {
                @NonNull
                @Override
                public String toString() {
                    return super.toString();
                }
            };
        }

        private void setCardData(@NonNull ItemCard card, @NonNull Item<?> item) {
            MainActivity activity = (MainActivity) card.getContext();

            card.name.setText(item.name);
            if (item.thumbnail != null) {
                activity.client.render(item.thumbnail.url).into(card.thumbnail);
            }
            String subtitle = this.getSubtitle(item);
            if (subtitle != null) {
                card.subtitle.setText(subtitle);
            }
        }

        @Nullable
        private String getSubtitle(@NonNull Item<?> item) {
            if (item instanceof AlbumItem album) {
                if (album.artist != null && !album.artist.name.isEmpty()) {
                    return album.artist.name;
                } else if (album.year != null) {
                    return album.year;
                }
            } else if (item instanceof ArtistItem artist) {
                if (artist.description != null) {
                    return artist.description;
                }
            } else if (item instanceof PlaylistItem playlist) {
                if (playlist.metadata != null) {
                    return playlist.metadata;
                } else if (playlist.author != null && !playlist.author.name.isEmpty()) {
                    return playlist.author.name;
                } else if (playlist.views != null) {
                    return playlist.views;
                }
            } else if (item instanceof SongItem song) {
                if (song.duration != null) {
                    return song.duration;
                } else if (song.artist != null && !song.artist.name.isEmpty()) {
                    return song.artist.name;
                } else if (song.album != null && !song.album.name.isEmpty()) {
                    return song.album.name;
                }
                return song.metadata;
            }
            return null;
        }

        @NonNull
        private Function<Context, ItemCard> typeToCard(@NonNull Item<?> item) {
            return switch (item.getType()) {
                case ARTIST -> ArtistCard::new;
                case ALBUM -> AlbumCard::new;
                case PLAYLIST -> PlaylistCard::new;
                case SONG -> SongCard::new;
            };
        }
    }
}
