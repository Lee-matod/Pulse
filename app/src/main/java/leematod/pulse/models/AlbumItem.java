package leematod.pulse.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicResponsiveHeaderRenderer;
import leematod.pulse.http.innertube.types.MusicTwoRowItemRenderer;
import leematod.pulse.http.innertube.types.NavigationEndpoint;
import leematod.pulse.http.innertube.types.Runs;
import leematod.pulse.http.innertube.types.Thumbnail;

import java.util.ArrayList;
import java.util.List;

public class AlbumItem extends Item<NavigationEndpoint.Browse> {
    public final @NonNull List<SongItem> songs;
    public @Nullable ArtistItem artist;
    public @Nullable String description;
    public @Nullable String year;
    public @Nullable String itemCount;
    public @Nullable String duration;

    public AlbumItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Browse endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams) {
        this(
                name,
                thumbnail,
                endpoint,
                clickTrackingParams,
                new ArrayList<>(),
                null,
                null,
                null,
                null,
                null);
    }

    public AlbumItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Browse endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams,
            @NonNull List<SongItem> songs,
            @Nullable ArtistItem artist,
            @Nullable String description,
            @Nullable String year,
            @Nullable String itemCount,
            @Nullable String duration) {
        super(name, thumbnail, endpoint, clickTrackingParams);
        this.songs = songs;
        this.artist = artist;
        this.description = description;
        this.year = year;
        this.itemCount = itemCount;
        this.duration = duration;
    }

    @CanIgnoreReturnValue
    @NonNull
    public AlbumItem populate(@NonNull MusicResponsiveHeaderRenderer header) {
        this.year = header.subtitle.runs.get(header.subtitle.runs.size() - 1).text;
        this.description = header.description.musicDescriptionShelfRenderer.description.getText();
        this.itemCount = header.secondSubtitle.runs.get(0).text;
        this.duration = header.secondSubtitle.runs.get(2).text;

        if (header.straplineTextOne != null) {
            Runs.Run artistRun = header.straplineTextOne.runs.get(0);
            NavigationEndpoint.Browse endpoint = null;
            TrackingData.ClickTrackingParams clickTrackingParams = null;
            if (artistRun.navigationEndpoint != null) {
                endpoint = artistRun.navigationEndpoint.browseEndpoint;
                clickTrackingParams = artistRun.navigationEndpoint.clickTrackingParams;
            }
            Thumbnail artistThumbnail = null;
            if (header.straplineThumbnailRenderer != null) {
                artistThumbnail =
                        header.straplineThumbnailRenderer.musicThumbnailRenderer.thumbnail
                                .thumbnails.get(0);
            }

            this.artist =
                    new ArtistItem(artistRun.text, artistThumbnail, endpoint, clickTrackingParams);
        }
        return this;
    }

    @CanIgnoreReturnValue
    @NonNull
    public AlbumItem populate(@NonNull MusicTwoRowItemRenderer renderer) {
        for (Runs.Run run : renderer.subtitle.runs) {
            if (run.navigationEndpoint != null && run.navigationEndpoint.browseEndpoint != null) {
                this.artist =
                        new ArtistItem(
                                run.text,
                                null,
                                run.navigationEndpoint.browseEndpoint,
                                run.navigationEndpoint.clickTrackingParams);
            }
        }
        return this;
    }

    @Nullable
    @Override
    public String getKey() {
        if (this.endpoint == null) {
            return null;
        }
        return this.endpoint.browseId;
    }

    @Override
    @NonNull
    public Type getType() {
        return Type.ALBUM;
    }
}
