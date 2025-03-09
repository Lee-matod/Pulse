package leematod.pulse.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.NavigationEndpoint;
import leematod.pulse.http.innertube.types.Thumbnail;

public abstract class Item<E extends NavigationEndpoint.EndpointType> {
    public final @NonNull String name;
    public final @Nullable Thumbnail thumbnail;
    public final @Nullable TrackingData.ClickTrackingParams clickTrackingParams;
    public final @Nullable E endpoint;

    public Item(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable E endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams) {
        this.name = name;
        this.thumbnail = thumbnail;
        this.endpoint = endpoint;
        this.clickTrackingParams = clickTrackingParams;
    }

    @Nullable
    public abstract String getKey();

    @NonNull
    public abstract Item.Type getType();

    public enum Type {
        ALBUM,
        ARTIST,
        PLAYLIST,
        SONG,
    }

    @FunctionalInterface
    public interface DefaultConstructor<
            I extends Item<E>, E extends NavigationEndpoint.EndpointType> {
        I init(
                @NonNull String name,
                @Nullable Thumbnail thumbnail,
                @Nullable E endpoint,
                @Nullable TrackingData.ClickTrackingParams clickTrackingParams);
    }
}
