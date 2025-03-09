package leematod.pulse.http.innertube.types.responses;

import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicShelfRenderer;

import java.util.List;

public final class PlaylistContinuationResponse {
    public TrackingData.TrackingParams trackingParams;
    public @Nullable List<ReceivedResponseAction> onResponseReceivedActions;

    public static final class ReceivedResponseAction {
        public AppendContinuationItems appendContinuationItemsAction;

        public static final class AppendContinuationItems {
            public List<MusicShelfRenderer.Content> continuationItems;
        }
    }
}
