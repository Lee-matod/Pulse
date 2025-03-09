package leematod.pulse.http.innertube.types;

import java.util.List;

public final class ThumbnailRenderer {
    public MusicThumbnailRenderer musicThumbnailRenderer;

    public Thumbnail get() {
        return this.musicThumbnailRenderer.thumbnail.thumbnails.get(0);
    }

    public static final class MusicThumbnailRenderer {
        public OuterThumbnail thumbnail;

        public static final class OuterThumbnail {
            public List<Thumbnail> thumbnails;
        }
    }
}
