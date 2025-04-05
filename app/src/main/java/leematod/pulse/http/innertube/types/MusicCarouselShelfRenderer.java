package leematod.pulse.http.innertube.types;

import androidx.annotation.Nullable;

import java.util.List;

public final class MusicCarouselShelfRenderer {
    public Header header;
    public List<Content> contents;

    public static final class Content {
        public @Nullable MusicTwoRowItemRenderer musicTwoRowItemRenderer;
        public @Nullable MusicResponsiveListItemRenderer musicResponsiveListItemRenderer;
    }

    public static final class Header {
        public MusicCarouselShelfBasicHeaderRenderer musicCarouselShelfBasicHeaderRenderer;

        public static final class MusicCarouselShelfBasicHeaderRenderer {
            public ButtonRenderer moreContentButton;
            public Runs title;
        }
    }
}
