package leematod.pulse.http.innertube.types;

import leematod.pulse.http.innertube.TrackingData;

public final class Continuation {
    public Data nextContinuationData;

    public static final class Data {
        public TrackingData.Continuation continuation;
    }
}
