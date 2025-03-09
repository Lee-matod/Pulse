package leematod.pulse.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicResponsiveListItemRenderer;
import leematod.pulse.http.innertube.types.NavigationEndpoint;
import leematod.pulse.http.innertube.types.Runs;
import leematod.pulse.http.innertube.types.Thumbnail;

import org.jetbrains.annotations.Contract;

import java.util.Objects;

public class SongItem extends Item<NavigationEndpoint.Watch> {
    public final @NonNull String metadata;
    public final @Nullable ArtistItem artist;
    public final @Nullable AlbumItem album;
    public final @Nullable String duration;

    public SongItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Watch endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams) {
        this(name, thumbnail, endpoint, clickTrackingParams, "", null, null, null);
    }

    public SongItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Watch endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams,
            @NonNull String metadata,
            @Nullable ArtistItem artist,
            @Nullable AlbumItem album,
            @Nullable String duration) {
        super(name, thumbnail, endpoint, clickTrackingParams);
        this.metadata = metadata;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
    }

    @NonNull
    @Contract("_ -> new")
    public static SongItem fromResponsiveList(@NonNull MusicResponsiveListItemRenderer renderer) {
        String name = "";
        TrackingData.ClickTrackingParams trackingParams = null;
        String metadata = "";
        ArtistItem artist = null;
        AlbumItem album = null;
        NavigationEndpoint.Watch endpoint = null;
        String duration = null;

        Thumbnail thumbnail;
        try {
            thumbnail = renderer.thumbnail.musicThumbnailRenderer.thumbnail.thumbnails.get(0);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            thumbnail = null;
        }

        if (renderer.fixedColumns != null) {
            duration =
                    renderer.fixedColumns
                            .get(0)
                            .musicResponsiveListItemFixedColumnRenderer
                            .text
                            .getText();
        }

        if (renderer.flexColumns != null) {
            for (MusicResponsiveListItemRenderer.FlexColumn column : renderer.flexColumns) {
                if (column.musicResponsiveListItemFlexColumnRenderer.text.runs == null) continue;
                for (Runs.Run run : column.musicResponsiveListItemFlexColumnRenderer.text.runs) {
                    if (run.navigationEndpoint == null) {
                        if (!run.text.isEmpty() && !Objects.equals(run.text, " • ")) {
                            metadata = run.text;
                        }
                        continue;
                    }
                    if (run.navigationEndpoint.watchEndpoint != null) {
                        name = run.text;
                        endpoint = run.navigationEndpoint.watchEndpoint;
                        trackingParams = run.navigationEndpoint.clickTrackingParams;
                        break;
                    } else if (run.navigationEndpoint.browseEndpoint != null) {
                        String title = run.text;
                        TrackingData.ClickTrackingParams clickTrackingParams =
                                run.navigationEndpoint.clickTrackingParams;
                        Item.Type pageType = run.navigationEndpoint.browseEndpoint.getPageType();
                        if (pageType != null) {
                            switch (pageType) {
                                case ALBUM:
                                    album =
                                            new AlbumItem(
                                                    title,
                                                    null,
                                                    run.navigationEndpoint.browseEndpoint,
                                                    clickTrackingParams);
                                    break;
                                case ARTIST:
                                    artist =
                                            new ArtistItem(
                                                    title,
                                                    null,
                                                    run.navigationEndpoint.browseEndpoint,
                                                    clickTrackingParams);
                            }
                        }
                    }
                }
            }
        }

        return new SongItem(
                name, thumbnail, endpoint, trackingParams, metadata, artist, album, duration);
    }

    @Nullable
    @Override
    public String getKey() {
        if (this.endpoint == null) {
            return null;
        }
        return this.endpoint.videoId;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.SONG;
    }
}
