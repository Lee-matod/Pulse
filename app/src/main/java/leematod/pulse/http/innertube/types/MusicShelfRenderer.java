package leematod.pulse.http.innertube.types;

import static leematod.pulse.Utils.filter;
import static leematod.pulse.Utils.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.models.SongItem;

import java.util.List;

public final class MusicShelfRenderer {
    public NavigationEndpoint bottomEndpoint;
    public List<Content> contents;
    public Runs title;

    @NonNull
    public List<SongItem> mapSongs() {
        return filter(map(this.contents, Content::toSong));
    }

    public static final class Content {
        public @Nullable MusicResponsiveListItemRenderer musicResponsiveListItemRenderer;
        public @Nullable ContinuationItemRenderer continuationItemRenderer;

        @Nullable
        public SongItem toSong() {
            if (this.musicResponsiveListItemRenderer == null) {
                return null;
            }
            return SongItem.fromResponsiveList(this.musicResponsiveListItemRenderer);
        }
    }
}
