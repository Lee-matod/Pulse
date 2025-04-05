package leematod.pulse.ui.layouts.playlist;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.R;
import leematod.pulse.ui.layouts.Layout;

public class PlaylistLayout extends Layout {
    public PlaylistLayout(Context context) {
        super(context);
        this.sidebar.addTab(R.string.playlist, R.drawable.playlist);
        this.sidebar.addTab(R.string.related, R.drawable.sparkles);
        this.sidebar.setActiveTab(0);
    }

    public void setPlaylist(@NonNull String playlistId) {
        if (this.sidebar.getActiveTabLayout() instanceof PlaylistScreen screen) {
            screen.setPlaylist(playlistId);
        }
    }

    @Nullable
    @Override
    public View sidebarCallback(int pos, @NonNull String name) {
        return switch (pos) {
            case 0 -> new PlaylistScreen(this.getContext());
            case 1 -> new RelatedPlaylistsScreen(this.getContext());
            default -> null;
        };
    }
}
