package leematod.pulse.http.innertube.types.bodies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.Context;

public class BrowseBody {
    public @NonNull Context context = new Context();
    public @Nullable String browseId;
    public @Nullable String params;
    public @Nullable String continuation;

    public BrowseBody() {
        this(null);
    }

    public BrowseBody(@Nullable String browseId) {
        this(browseId, null);
    }

    public BrowseBody(@Nullable String browseId, @Nullable TrackingData.Params params) {
        this(browseId, params, null);
    }

    public BrowseBody(
            @Nullable String browseId,
            @Nullable TrackingData.Params params,
            @Nullable TrackingData.Continuation continuation) {
        this.browseId = browseId;
        if (params != null) this.params = params.value();
        if (continuation != null) this.continuation = continuation.value();
    }
}
