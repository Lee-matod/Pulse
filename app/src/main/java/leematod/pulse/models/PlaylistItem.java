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

public class PlaylistItem extends Item<NavigationEndpoint.Browse> {
    public final @NonNull List<SongItem> items;
    public final @NonNull List<PlaylistItem> relatedPlaylists;
    public @Nullable ArtistItem author;
    public @Nullable String views;
    public @Nullable String year;
    public @Nullable String itemCount;
    public @Nullable String duration;
    public @Nullable String metadata;

    public PlaylistItem(
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
                null,
                null);
    }

    public PlaylistItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Browse endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams,
            @NonNull List<SongItem> items,
            @Nullable ArtistItem author,
            @Nullable String views,
            @Nullable String year,
            @Nullable String itemCount,
            @Nullable String duration,
            @Nullable String metadata) {
        super(name, thumbnail, endpoint, clickTrackingParams);
        this.items = items;
        this.author = author;
        this.views = views;
        this.year = year;
        this.itemCount = itemCount;
        this.duration = duration;
        this.metadata = metadata;
        this.relatedPlaylists = new ArrayList<>();
    }

    @CanIgnoreReturnValue
    @NonNull
    public PlaylistItem populate(@NonNull MusicTwoRowItemRenderer renderer) {
        if (renderer.subtitle.runs.size() == 1) {
            // List of authors inside the playlist, but only as names. No extra info
            this.metadata = renderer.subtitle.getText();
        } else if (renderer.subtitle.runs.size() == 3 || renderer.subtitle.runs.size() == 5) {

            List<Runs.Run> runs;
            if (renderer.subtitle.runs.size() == 5) {
                // First two items are "Playlist" text followed by a separator. We remove them
                // because the following items are in the exact order as we want them to be.
                runs = renderer.subtitle.runs.subList(2, 5);
            } else {
                runs = renderer.subtitle.runs;
            }
            this.views = runs.get(2).text;
            Runs.Run authorInfo = runs.get(0);
            if (authorInfo.navigationEndpoint != null) {
                this.author =
                        new ArtistItem(
                                authorInfo.text,
                                null,
                                authorInfo.navigationEndpoint.browseEndpoint,
                                authorInfo.navigationEndpoint.clickTrackingParams);
            }
        }
        return this;
    }

    @CanIgnoreReturnValue
    @NonNull
    public PlaylistItem populate(@NonNull MusicResponsiveHeaderRenderer header) {
        this.metadata = header.description.musicDescriptionShelfRenderer.description.getText();
        this.year = header.subtitle.runs.get(2).text;
        if (header.facepile != null) {
            this.author = ArtistItem.fromAvatarViewModel(header.facepile.avatarStackViewModel);
            if (header.facepile.avatarStackViewModel.avatars == null) {
                // YouTube Music-created playlist. Some metadata is switched around.
                this.views = null;
                this.itemCount = header.secondSubtitle.runs.get(0).text;
                this.duration = header.secondSubtitle.runs.get(2).text;
            } else {
                this.views = header.secondSubtitle.runs.get(0).text;
                this.itemCount = header.secondSubtitle.runs.get(2).text;
                this.duration = header.secondSubtitle.runs.get(4).text;
            }
        }
        return this;
    }

    public boolean isOfficialPlaylist() {
        return (this.author != null && this.author.name.equals("YouTube Music"))
                || (this.views == null && (this.itemCount != null || this.duration != null));
    }

    @Nullable
    @Override
    public String getKey() {
        if (this.endpoint == null) return null;
        return this.endpoint.browseId;
    }

    @NonNull
    @Override
    public Type getType() {
        return Type.PLAYLIST;
    }
}
