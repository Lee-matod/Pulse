package leematod.pulse.http.innertube.types;

import static leematod.pulse.Utils.nullsafe;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.models.ArtistItem;
import leematod.pulse.models.Item;
import leematod.pulse.models.ItemGroup;
import leematod.pulse.models.PlaylistItem;
import leematod.pulse.models.SongItem;

import java.util.ArrayList;
import java.util.List;

public final class SectionListRenderer {
    public List<Content> contents;
    public List<Continuation> continuations;

    @Nullable
    public TrackingData.Continuation getContinuation() {
        try {
            return this.continuations.get(0).nextContinuationData.continuation;
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            return null;
        }
    }

    @NonNull
    public List<ItemGroup> mapAmbiguously() {
        List<ItemGroup> groups = new ArrayList<>();
        for (SectionListRenderer.Content content : contents) {
            String title;
            NavigationEndpoint.Browse moreContent;
            List<Item<?>> items = new ArrayList<>();

            if (content.musicCarouselShelfRenderer != null) {
                title =
                        content.musicCarouselShelfRenderer.header
                                .musicCarouselShelfBasicHeaderRenderer.title.getText();
                moreContent =
                        nullsafe(
                                () ->
                                        content.musicCarouselShelfRenderer
                                                .header
                                                .musicCarouselShelfBasicHeaderRenderer
                                                .moreContentButton
                                                .buttonRenderer
                                                .navigationEndpoint
                                                .browseEndpoint);

                for (MusicCarouselShelfRenderer.Content itemContent :
                        content.musicCarouselShelfRenderer.contents) {
                    if (itemContent.musicResponsiveListItemRenderer != null) {
                        items.add(
                                SongItem.fromResponsiveList(
                                        itemContent.musicResponsiveListItemRenderer));
                    } else if (itemContent.musicTwoRowItemRenderer != null) {
                        if (itemContent.musicTwoRowItemRenderer.navigationEndpoint.browseEndpoint
                                == null) {
                            continue;
                        }
                        Item.Type pageType =
                                itemContent.musicTwoRowItemRenderer.navigationEndpoint
                                        .browseEndpoint.getPageType();
                        if (pageType == null) {
                            continue;
                        }
                        Item<?> item =
                                switch (pageType) {
                                    case ALBUM ->
                                            itemContent
                                                    .musicTwoRowItemRenderer
                                                    .apply(AlbumItem::new)
                                                    .populate(itemContent.musicTwoRowItemRenderer);
                                    case ARTIST ->
                                            itemContent
                                                    .musicTwoRowItemRenderer
                                                    .apply(ArtistItem::new)
                                                    .populate(itemContent.musicTwoRowItemRenderer);
                                    case PLAYLIST ->
                                            itemContent
                                                    .musicTwoRowItemRenderer
                                                    .apply(PlaylistItem::new)
                                                    .populate(itemContent.musicTwoRowItemRenderer);
                                    default -> null;
                                };
                        if (item == null) {
                            continue;
                        }
                        items.add(item);
                    }
                }
            } else if (content.musicShelfRenderer != null) {
                title = content.musicShelfRenderer.title.getText();
                moreContent =
                        nullsafe(() -> content.musicShelfRenderer.bottomEndpoint.browseEndpoint);
                items.addAll(content.musicShelfRenderer.mapSongs());
            } else {
                continue;
            }
            groups.add(new ItemGroup(title, items, moreContent));
        }

        return groups;
    }

    public static final class Content {
        public @Nullable MusicCarouselShelfRenderer musicCarouselShelfRenderer;
        public @Nullable MusicShelfRenderer musicShelfRenderer;
        public @Nullable MusicResponsiveHeaderRenderer musicResponsiveHeaderRenderer;
        public @Nullable MusicPlaylistShelfRenderer musicPlaylistShelfRenderer;
    }
}
