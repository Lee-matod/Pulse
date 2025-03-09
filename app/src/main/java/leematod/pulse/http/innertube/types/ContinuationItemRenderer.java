package leematod.pulse.http.innertube.types;

import leematod.pulse.http.innertube.TrackingData;

public final class ContinuationItemRenderer {
    public ContinuationEndpoint continuationEndpoint;
    public TrackingData.Token getToken() {
        return this.continuationEndpoint.continuationCommand.token;
    }

    public static final class ContinuationEndpoint {
        public String clickingParams;
        public ContinuationCommand continuationCommand;

        public static final class ContinuationCommand {
            public TrackingData.Token token;
        }
    }
}
