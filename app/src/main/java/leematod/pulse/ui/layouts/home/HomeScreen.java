package leematod.pulse.ui.layouts.home;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import leematod.pulse.MainActivity;
import leematod.pulse.http.innertube.InnertubeResponse;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.models.ArtistItem;
import leematod.pulse.models.ItemGroup;
import leematod.pulse.models.SongItem;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.DynamicRecyclerAdapter;
import leematod.pulse.ui.cards.CardDisplay;
import leematod.pulse.ui.components.ContainerizedLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class HomeScreen extends ContainerizedLayout {
    protected static @Nullable InnertubeResponse<List<ItemGroup>> result = null;
    public final @NonNull RecyclerView container;
    protected final @NonNull ContainerAdapter adapter;
    protected final @NonNull MainActivity activity;

    public HomeScreen(@NonNull Context context) {
        super(context);

        this.activity = (MainActivity) context;

        this.adapter = new ContainerAdapter();
        this.adapter.setStartupDummies(4);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        this.container = new RecyclerView(context);
        this.container.setLayoutManager(layoutManager);
        this.container.setAdapter(this.adapter);

        this.addView(this.container);
        this.setOnScrollChangeListener((OnScrollChangeListener) this::onScrollChange);

        if (HomeScreen.categories().isEmpty()) {
            this.activity.client.browseMusic().thenAccept(this::updateSuggestions);
        } else {
            this.updateSuggestions(new ArrayList<>());
        }
    }

    @NonNull
    public static List<ItemGroup> categories() {
        if (HomeScreen.result == null) {
            return new ArrayList<>();
        }
        return Objects.requireNonNullElseGet(HomeScreen.result.response, ArrayList::new);
    }

    public static boolean hasContinuation() {
        if (HomeScreen.result == null) {
            return false;
        }
        return HomeScreen.result.hasNext();
    }

    protected void updateSuggestions(@NonNull List<ItemGroup> items) {
        this.updateSuggestions(new InnertubeResponse<>(items, null, null, (a, b) -> a));
    }

    @SuppressLint("NotifyDataSetChanged")
    protected void updateSuggestions(@NonNull InnertubeResponse<List<ItemGroup>> response) {
        if ((HomeScreen.result == null || !HomeScreen.hasContinuation())
                && response.response != null
                && !response.response.isEmpty()) {
            HomeScreen.result = response;
        }
        this.post(
                () -> {
                    this.adapter.willLoadMoreContent(HomeScreen.hasContinuation());
                    this.adapter.notifyDataSetChanged();
                });
    }

    protected void onScrollChange(
            View view, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) this.container.getLayoutManager();
        if (layoutManager == null) {
            return;
        }
        int lastPos = layoutManager.findLastCompletelyVisibleItemPosition();
        if (lastPos == layoutManager.getItemCount() - 1) {
            View lastView = layoutManager.findViewByPosition(lastPos);
            if (lastView == null) {
                return;
            }
            float posY = lastView.getY() + this.container.getY() - scrollY;
            int visible = this.getHeight() - (int) posY;
            if (visible > lastView.getHeight() / 2) {
                if (HomeScreen.result != null && HomeScreen.result.hasNext()) {
                    CompletableFuture<List<ItemGroup>> fut = HomeScreen.result.next();
                    if (fut == null) {
                        return;
                    }
                    fut.thenAccept(this::updateSuggestions);
                }
            }
        }
    }

    public static class ContainerAdapter extends DynamicRecyclerAdapter<ItemGroup, RelativeLayout> {
        @Override
        public int getRealItemCount() {
            return HomeScreen.categories().size();
        }

        @NonNull
        @Override
        public ItemGroup getItemAt(int position) {
            return HomeScreen.categories().get(position);
        }

        @NonNull
        @Override
        public ItemGroup getDummyAt(int position) {
            ItemGroup group = new ItemGroup("Container");
            switch (position) {
                case 0:
                    // TODO: add Recent Items container. Should also implement ItemCard.small
                    break;
                case 1:
                    for (int i = 0; i < 4; i++) {
                        group.items.add(new SongItem("Song Item", null, null, null));
                    }
                    break;
                case 2:
                    for (int i = 0; i < 5; i++) {
                        group.items.add(new ArtistItem("Artist Item", null, null, null));
                    }
                    break;
                default:
                    for (int i = 0; i < 5; i++) {
                        group.items.add(new AlbumItem("Album Item", null, null, null));
                    }
                    break;
            }
            return group;
        }

        @Override
        public void bindView(
                @NonNull ItemGroup group,
                @NonNull RelativeLayout view,
                int position,
                boolean isDummy) {
            view.removeAllViews();

            Context context = view.getContext();
            RelativeLayout.LayoutParams displayParams = new RelativeLayout.LayoutParams(-2, -2);

            CardDisplay display = new CardDisplay(context);
            display.setItems(group);

            RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(-2, -2);
            titleParams.topMargin = pixels(8);
            titleParams.bottomMargin = pixels(8);

            TextView title = ensureId(new TextView(context));
            title.setText(group.name);
            title.setTextSize(24);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setTextColor(ColorPalette.current.text);
            title.setBackgroundColor(ColorPalette.current.background);
            displayParams.addRule(RelativeLayout.BELOW, title.getId());

            view.addView(title, titleParams);
            view.addView(display, displayParams);
        }

        @NonNull
        @Override
        public DynamicViewHolder<RelativeLayout> onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            return new DynamicViewHolder<>(new RelativeLayout(parent.getContext()));
        }
    }
}
