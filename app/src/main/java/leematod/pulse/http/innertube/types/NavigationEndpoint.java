package leematod.pulse.http.innertube.types;

import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.models.Item;

import java.util.Objects;

public final class NavigationEndpoint {
    public TrackingData.ClickTrackingParams clickTrackingParams;
    public @Nullable Watch watchEndpoint;
    public @Nullable WatchPlaylist watchPlaylistEndpoint;
    public @Nullable Browse browseEndpoint;
    public @Nullable Search searchEndpoint;

    public abstract static class EndpointType {
        public @Nullable TrackingData.Params params;
    }

    public static final class Watch extends EndpointType {
        public String playlistId;
        public String videoId;
        public Integer index;
        public String playlistSetVideoId;
        public WatchEndpointMusicSupportedConfigs watchEndpointMusicSupportedConfigs;

        public static final class WatchEndpointMusicSupportedConfigs {
            public WatchEndpointMusicConfig watchEndpointMusicConfig;

            public static class WatchEndpointMusicConfig {
                public String musicVideoType;
            }
        }
    }

    public static final class WatchPlaylist extends EndpointType {
        public String playlistId;
    }

    public static final class Browse extends EndpointType {
        public String browseId;
        public BrowseEndpointContextSupportedConfigs browseEndpointContextSupportedConfigs;

        @Nullable
        public Item.Type getPageType() {
            String pageType;
            try {
                pageType =
                        this.browseEndpointContextSupportedConfigs
                                .browseEndpointContextMusicConfig
                                .pageType;
            } catch (NullPointerException e) {
                return null;
            }
            if (Objects.equals(pageType, "MUSIC_PAGE_TYPE_ALBUM")) {
                return Item.Type.ALBUM;
            } else if (Objects.equals(pageType, "MUSIC_PAGE_TYPE_ARTIST")) {
                return Item.Type.ARTIST;
            } else if (Objects.equals(pageType, "MUSIC_PAGE_TYPE_PLAYLIST")) {
                return Item.Type.PLAYLIST;
            }
            return null;
        }

        public static final class BrowseEndpointContextSupportedConfigs {
            public BrowseEndpointContextMusicConfig browseEndpointContextMusicConfig;

            public static final class BrowseEndpointContextMusicConfig {
                public String pageType;
            }
        }
    }

    public static final class Search extends EndpointType {
        public String query;
    }
}
