package leematod.pulse.http.innertube.types;

import static leematod.pulse.Utils.filter;
import static leematod.pulse.Utils.map;

import androidx.annotation.NonNull;

import leematod.pulse.models.SongItem;

import java.util.List;

public final class MusicPlaylistShelfRenderer {
    public List<MusicShelfRenderer.Content> contents;

    @NonNull
    public List<SongItem> mapSongs() {
        return filter(map(this.contents, MusicShelfRenderer.Content::toSong));
    }
}
