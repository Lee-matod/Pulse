package leematod.pulse.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicImmersiveHeaderRenderer;
import leematod.pulse.http.innertube.types.MusicResponsiveHeaderRenderer;
import leematod.pulse.http.innertube.types.MusicTwoRowItemRenderer;
import leematod.pulse.http.innertube.types.NavigationEndpoint;
import leematod.pulse.http.innertube.types.Thumbnail;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class ArtistItem extends Item<NavigationEndpoint.Browse> {
    public final @NonNull List<ItemGroup> items;
    public @Nullable String description;
    public @Nullable NavigationEndpoint.Watch playButton;
    public @Nullable NavigationEndpoint.Watch startRadio;

    public ArtistItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Browse endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams) {
        this(name, thumbnail, endpoint, clickTrackingParams, new ArrayList<>(), null, null, null);
    }

    public ArtistItem(
            @NonNull String name,
            @Nullable Thumbnail thumbnail,
            @Nullable NavigationEndpoint.Browse endpoint,
            @Nullable TrackingData.ClickTrackingParams clickTrackingParams,
            @NonNull List<ItemGroup> items,
            @Nullable String description,
            @Nullable NavigationEndpoint.Watch playButton,
            @Nullable NavigationEndpoint.Watch startRadio) {
        super(name, thumbnail, endpoint, clickTrackingParams);
        this.items = items;
        this.description = description;
        this.playButton = playButton;
        this.startRadio = startRadio;
    }

    @NonNull
    @Contract("_ -> new")
    public static ArtistItem fromAvatarViewModel(
            @NonNull MusicResponsiveHeaderRenderer.HeaderFacepile.AvatarStackModel model) {
        String name = model.text.content;
        Thumbnail thumbnail = null;
        NavigationEndpoint.Browse endpoint = null;
        TrackingData.ClickTrackingParams trackingParams = null;

        if (model.avatars != null) {
            String avatarUrl = model.avatars.get(0).avatarViewModel.image.sources.get(0).url;
            thumbnail = new Thumbnail();
            thumbnail.url = avatarUrl;
            thumbnail.width = 48;
            thumbnail.height = 48;
        }

        if (model.rendererContext.onTap != null) {
            endpoint = model.rendererContext.onTap.innertubeCommand.browseEndpoint;
            trackingParams = model.rendererContext.onTap.innertubeCommand.clickTrackingParams;
        }

        return new ArtistItem(name, thumbnail, endpoint, trackingParams);
    }

    @CanIgnoreReturnValue
    @NonNull
    public ArtistItem populate(@NonNull MusicTwoRowItemRenderer renderer) {
        this.description = renderer.subtitle.getText();
        return this;
    }

    @CanIgnoreReturnValue
    @NonNull
    public ArtistItem populate(@NonNull MusicImmersiveHeaderRenderer renderer) {
        this.description = renderer.description.getText();
        this.playButton = renderer.playButton.navigationEndpoint.watchEndpoint;
        this.startRadio = renderer.startRadioButton.navigationEndpoint.watchEndpoint;
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

    @NonNull
    @Override
    public Type getType() {
        return Type.ARTIST;
    }
}
