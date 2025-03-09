package leematod.pulse.http.innertube.types;

import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;

import java.util.List;

public final class MusicResponsiveListItemRenderer {
    public @Nullable List<FixedColumn> fixedColumns;
    public @Nullable List<FlexColumn> flexColumns;
    public ThumbnailRenderer thumbnail;
    public TrackingData.TrackingParams trackingParams;

    public static final class FlexColumn {
        public MusicResponsiveListItemFlexColumnRenderer musicResponsiveListItemFlexColumnRenderer;

        public static final class MusicResponsiveListItemFlexColumnRenderer {
            public Runs text;
        }
    }

    public static final class FixedColumn {
        public MusicResponsiveListItemFixedColumnRenderer
                musicResponsiveListItemFixedColumnRenderer;

        public static final class MusicResponsiveListItemFixedColumnRenderer {
            public Runs text;
        }
    }
}
