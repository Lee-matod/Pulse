package leematod.pulse.http.innertube.types;

import androidx.annotation.NonNull;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.models.Item;

public final class MusicTwoRowItemRenderer {
    public NavigationEndpoint navigationEndpoint;
    public ThumbnailRenderer thumbnailRenderer;
    public Runs title;
    public Runs subtitle;
    public TrackingData.TrackingParams trackingParams;

    public <I extends Item<NavigationEndpoint.Browse>> I apply(
            @NonNull Item.DefaultConstructor<I, NavigationEndpoint.Browse> constructor) {
        String name = this.title.getText();
        Thumbnail thumbnail = this.thumbnailRenderer.get();
        NavigationEndpoint.Browse endpoint;
        TrackingData.ClickTrackingParams params;
        if (this.navigationEndpoint != null) {
            endpoint = this.navigationEndpoint.browseEndpoint;
            params = this.navigationEndpoint.clickTrackingParams;
        } else {
            endpoint = null;
            params = null;
        }
        return constructor.init(name, thumbnail, endpoint, params);
    }
}
