package leematod.pulse.http.innertube.types.responses;

import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.http.innertube.types.MusicImmersiveHeaderRenderer;
import leematod.pulse.http.innertube.types.SectionListRenderer;
import leematod.pulse.http.innertube.types.Tabs;

import java.util.List;

public final class BrowseResponse {
    public Contents contents;
    public TrackingData.TrackingParams trackingParams;
    public @Nullable ContinuationContents continuationContents;
    public @Nullable Header header;

    public static final class Header {
        public MusicImmersiveHeaderRenderer musicImmersiveHeaderRenderer;
    }

    public static final class Contents {
        public Tabs singleColumnBrowseResultsRenderer;
        public TwoColumnResultsRenderer twoColumnBrowseResultsRenderer;

        public static final class TwoColumnResultsRenderer {
            public SecondaryContents secondaryContents;
            public List<Tabs.Tab> tabs;

            public static final class SecondaryContents {
                public SectionListRenderer sectionListRenderer;
            }
        }
    }

    public static final class ContinuationContents {
        public SectionListRenderer sectionListContinuation;
    }
}
