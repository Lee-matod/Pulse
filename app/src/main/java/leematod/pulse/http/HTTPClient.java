package leematod.pulse.http;

import static leematod.pulse.Utils.filter;
import static leematod.pulse.Utils.map;

import android.app.Activity;
import android.util.MalformedJsonException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.squareup.picasso3.Picasso;
import com.squareup.picasso3.RequestCreator;

import leematod.pulse.http.innertube.Innertube;
import leematod.pulse.http.innertube.InnertubeResponse;
import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicCarouselShelfRenderer;
import leematod.pulse.http.innertube.types.MusicImmersiveHeaderRenderer;
import leematod.pulse.http.innertube.types.MusicResponsiveHeaderRenderer;
import leematod.pulse.http.innertube.types.MusicShelfRenderer;
import leematod.pulse.http.innertube.types.SectionListRenderer;
import leematod.pulse.http.innertube.types.Tabs;
import leematod.pulse.http.innertube.types.Thumbnail;
import leematod.pulse.http.innertube.types.bodies.BrowseBody;
import leematod.pulse.http.innertube.types.responses.BrowseResponse;
import leematod.pulse.http.innertube.types.responses.PlaylistContinuationResponse;
import leematod.pulse.models.AlbumItem;
import leematod.pulse.models.ArtistItem;
import leematod.pulse.models.ItemGroup;
import leematod.pulse.models.PlaylistItem;
import leematod.pulse.models.SongItem;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class HTTPClient {
    protected final OkHttpClient client;
    protected final Picasso picasso;

    public HTTPClient(Activity activity) {
        this.client = new OkHttpClient();
        this.picasso = new Picasso.Builder(activity).client(this.client).build();
    }

    public RequestCreator render(@NonNull String url) {
        return this.picasso.load(url);
    }

    public CompletableFuture<ArtistItem> artist(@NonNull String artistId) {
        return CompletableFuture.supplyAsync(() -> this.artist(new BrowseBody(artistId)));
    }

    @Nullable
    protected ArtistItem artist(@NonNull BrowseBody body) {
        Innertube route = new Innertube("/browse", body);
        route.setMask(
                "contents.singleColumnBrowseResultsRenderer.tabs.tabRenderer.content."
                        + "sectionListRenderer.contents(musicShelfRenderer(title,contents."
                        + Innertube.MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER
                        + ",bottomEndpoint),musicCarouselShelfRenderer(header."
                        + "musicCarouselShelfBasicHeaderRenderer.title,contents."
                        + Innertube.MASK_MUSIC_TWO_ROW_ITEM_RENDERER
                        + "),musicDescriptionShelfRenderer(header,subheader,description)),"
                        + "header.musicImmersiveHeaderRenderer(title,description,thumbnail,"
                        + "playButton,startRadioButton),trackingParams");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException e) {
            return null;
        }
        if (response.contents == null || response.header == null) {
            return null;
        }

        MusicImmersiveHeaderRenderer renderer = response.header.musicImmersiveHeaderRenderer;

        String name = renderer.title.getText();
        Thumbnail thumbnail = renderer.thumbnail.get();
        // There is a musicDescriptionShelfRenderer at the very bottom of the list, which won't get
        // recognized by mapAmbiguously. However, at least at the time of writing this, that
        // renderer does not contain any new/useful information that cannot be found elsewhere in
        // the returned package.
        List<ItemGroup> items =
                response.contents
                        .singleColumnBrowseResultsRenderer
                        .tabs
                        .get(0)
                        .tabRenderer
                        .content
                        .sectionListRenderer
                        .mapAmbiguously();

        return new ArtistItem(name, thumbnail, null, null, items, null, null, null)
                .populate(renderer);
    }

    public CompletableFuture<AlbumItem> album(@NonNull String albumId) {
        return CompletableFuture.supplyAsync(() -> this.album(new BrowseBody(albumId)));
    }

    @Nullable
    protected AlbumItem album(@NonNull BrowseBody body) {
        Innertube route = new Innertube("/browse", body);
        route.setMask(
                "contents.twoColumnBrowseResultsRenderer(secondaryContents.sectionListRenderer."
                        + "contents.musicShelfRenderer.contents."
                        + Innertube.MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER
                        + ",tabs.tabRenderer.content.sectionListRenderer.contents."
                        + Innertube.MASK_MUSIC_RESPONSIVE_HEADER_RENDERER
                        + "),trackingParams");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException e) {
            return null;
        }
        BrowseResponse.Contents.TwoColumnResultsRenderer renderer =
                response.contents.twoColumnBrowseResultsRenderer;
        if (renderer == null) {
            return null;
        }

        MusicResponsiveHeaderRenderer header;
        try {
            header =
                    renderer.tabs
                            .get(0)
                            .tabRenderer
                            .content
                            .sectionListRenderer
                            .contents
                            .get(0)
                            .musicResponsiveHeaderRenderer;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        if (header == null) {
            return null;
        }

        SectionListRenderer.Content parent;
        try {
            parent = renderer.secondaryContents.sectionListRenderer.contents.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        if (parent.musicPlaylistShelfRenderer == null) {
            return null;
        }

        AlbumItem item = header.apply(AlbumItem::new).populate(header);
        item.songs.addAll(parent.musicPlaylistShelfRenderer.mapSongs());
        return item;
    }

    public CompletableFuture<List<PlaylistItem>> relatedPlaylists(
            @NonNull InnertubeResponse<PlaylistItem> playlist) {
        return CompletableFuture.supplyAsync(
                () -> this.relatedPlaylists(new BrowseBody(), playlist));
    }

    @Nullable
    protected List<PlaylistItem> relatedPlaylists(
            @NonNull BrowseBody body, @NonNull InnertubeResponse<PlaylistItem> playlist) {
        TrackingData.Continuation continuation = playlist.data.continuation;
        if (continuation == null) {
            return null;
        }
        Innertube route = new Innertube("/browse", body);
        route.addParam("ctoken", continuation.value());
        route.addParam("continuation", continuation.value());
        route.addParam("type", "next");
        route.setMask(
                "continuationContents.sectionListContinuation.contents.musicCarouselShelfRenderer."
                        + "contents."
                        + Innertube.MASK_MUSIC_TWO_ROW_ITEM_RENDERER
                        + ",trackingParams");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException e) {
            return null;
        }

        if (response.continuationContents == null) {
            return null;
        }

        MusicCarouselShelfRenderer renderer =
                response.continuationContents.sectionListContinuation.contents.get(0)
                        .musicCarouselShelfRenderer;
        if (renderer == null) {
            return null;
        }

        return map(
                filter(renderer.contents, c -> c.musicTwoRowItemRenderer != null),
                c -> {
                    assert c.musicTwoRowItemRenderer != null;
                    PlaylistItem item = c.musicTwoRowItemRenderer.apply(PlaylistItem::new);
                    return item.populate(c.musicTwoRowItemRenderer);
                });
    }

    public CompletableFuture<InnertubeResponse<PlaylistItem>> playlist(@NonNull String playlistId) {
        return CompletableFuture.supplyAsync(() -> this.playlist(new BrowseBody(playlistId)));
    }

    @Nullable
    protected InnertubeResponse<PlaylistItem> playlist(@NonNull BrowseBody body) {
        Innertube route = new Innertube("/browse", body);
        route.setMask(
                "contents.twoColumnBrowseResultsRenderer(secondaryContents.sectionListRenderer("
                        + "contents.musicPlaylistShelfRenderer.contents("
                        + Innertube.MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER
                        + ",continuationItemRenderer),continuations)"
                        + ",tabs.tabRenderer.content.sectionListRenderer.contents."
                        + Innertube.MASK_MUSIC_RESPONSIVE_HEADER_RENDERER
                        + "),trackingParams");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException e) {
            return null;
        }

        BrowseResponse.Contents.TwoColumnResultsRenderer renderer =
                response.contents.twoColumnBrowseResultsRenderer;
        if (renderer == null) {
            return null;
        }

        MusicResponsiveHeaderRenderer header;
        try {
            header =
                    renderer.tabs
                            .get(0)
                            .tabRenderer
                            .content
                            .sectionListRenderer
                            .contents
                            .get(0)
                            .musicResponsiveHeaderRenderer;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        if (header == null) {
            return null;
        }

        SectionListRenderer.Content parent;
        try {
            parent = renderer.secondaryContents.sectionListRenderer.contents.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        if (parent.musicPlaylistShelfRenderer == null) {
            return null;
        }

        PlaylistItem item = header.apply(PlaylistItem::new).populate(header);
        item.items.addAll(parent.musicPlaylistShelfRenderer.mapSongs());

        InnertubeResponse<PlaylistItem> ret =
                new InnertubeResponse<>(
                        item,
                        response.trackingParams,
                        renderer.secondaryContents.sectionListRenderer.getContinuation(),
                        this::continuePlaylist);

        List<MusicShelfRenderer.Content> contents = parent.musicPlaylistShelfRenderer.contents;
        MusicShelfRenderer.Content last = contents.get(contents.size() - 1);
        if (last.continuationItemRenderer != null) {
            ret.data.token = last.continuationItemRenderer.getToken();
        }
        return ret;
    }

    @Nullable
    protected PlaylistItem continuePlaylist(
            @NonNull PlaylistItem playlist, @NonNull TrackingData data) {
        TrackingData.Token token = data.token;
        if (token == null) {
            return null;
        }
        Innertube route = new Innertube("/browse", new BrowseBody());
        route.setMask(
                "onResponseReceivedActions.appendContinuationItemsAction.continuationItems("
                        + Innertube.MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER
                        + ",continuationItemRenderer)");

        PlaylistContinuationResponse response;
        try {
            response = this.post(route, PlaylistContinuationResponse.class);
        } catch (IOException exc) {
            return null;
        }
        if (response.onResponseReceivedActions == null) {
            data.token = null;
            return null;
        }

        List<MusicShelfRenderer.Content> continuationItems =
                response.onResponseReceivedActions.get(0)
                        .appendContinuationItemsAction
                        .continuationItems;

        MusicShelfRenderer.Content last = continuationItems.get(continuationItems.size() - 1);
        if (last.continuationItemRenderer != null) {
            data.token =
                    last.continuationItemRenderer.continuationEndpoint.continuationCommand.token;
        } else {
            data.token = null;
        }

        List<SongItem> additionalItems =
                map(
                        filter(continuationItems, c -> c.musicResponsiveListItemRenderer != null),
                        i -> {
                            assert i.musicResponsiveListItemRenderer != null;
                            return SongItem.fromResponsiveList(i.musicResponsiveListItemRenderer);
                        });
        playlist.items.addAll(additionalItems);
        return playlist;
    }

    public CompletableFuture<InnertubeResponse<List<ItemGroup>>> browseMusic() {
        return CompletableFuture.supplyAsync(() -> this.browseMusic(new BrowseBody()));
    }

    @Nullable
    protected InnertubeResponse<List<ItemGroup>> browseMusic(@NonNull BrowseBody body) {
        Innertube route = new Innertube("/browse", body);
        route.setMask(
                "contents.singleColumnBrowseResultsRenderer.tabs(tabRenderer.content."
                        + "sectionListRenderer(contents(musicCarouselShelfRenderer("
                        + "header.musicCarouselShelfBasicHeaderRenderer.title.runs,contents("
                        + Innertube.MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER
                        + ","
                        + Innertube.MASK_MUSIC_TWO_ROW_ITEM_RENDERER
                        + "))),continuations("
                        + "nextContinuationData.continuation))),trackingParams");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException e) {
            return null;
        }

        Tabs.Tab tab;
        try {
            tab = response.contents.singleColumnBrowseResultsRenderer.tabs.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }

        SectionListRenderer listRenderer = tab.tabRenderer.content.sectionListRenderer;
        List<ItemGroup> groups = listRenderer.mapAmbiguously();
        TrackingData.Continuation continuation = listRenderer.getContinuation();
        return new InnertubeResponse<>(
                filter(groups, (g) -> !g.items.isEmpty()),
                response.trackingParams,
                continuation,
                this::continueMusicBrowsing);
    }

    @Nullable
    protected List<ItemGroup> continueMusicBrowsing(
            @NonNull List<ItemGroup> groups, @NonNull TrackingData data) {
        TrackingData.Continuation continuation = data.continuation;
        assert continuation != null;

        Innertube route = new Innertube("/browse", new BrowseBody());
        route.addParam("ctoken", continuation.value());
        route.addParam("continuation", continuation.value());
        route.addParam("type", "next");
        route.setMask(
                "continuationContents.sectionListContinuation("
                        + "contents.musicCarouselShelfRenderer("
                        + "header.musicCarouselShelfBasicHeaderRenderer.title,contents."
                        + Innertube.MASK_MUSIC_TWO_ROW_ITEM_RENDERER
                        + "),continuations)");

        BrowseResponse response;
        try {
            response = this.post(route, BrowseResponse.class);
        } catch (IOException exc) {
            return null;
        }
        if (response.continuationContents == null) {
            return null;
        }

        groups.addAll(
                filter(
                        response.continuationContents.sectionListContinuation.mapAmbiguously(),
                        (g) -> !g.items.isEmpty()));
        data.continuation = response.continuationContents.sectionListContinuation.getContinuation();
        return groups;
    }

    protected <T> T post(@NonNull Route route, @NonNull Class<T> schema) throws IOException {
        Object data = route.getBody();
        String dataType = route.getBodyType();
        if (data == null || dataType == null) {
            throw new MalformedJsonException("JSON is empty");
        }
        RequestBody body = RequestBody.create(new Gson().toJson(data), MediaType.parse(dataType));
        Request request =
                new Request.Builder()
                        .url(route.getTarget())
                        .headers(Headers.of(route.getHeaders()))
                        .post(body)
                        .build();
        Call call = this.client.newCall(request);

        try (Response response = call.execute()) {
            String json = response.body().string();
            Gson engine = route.engine();
            return engine.fromJson(json, schema);
        }
    }
}
