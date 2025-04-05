package leematod.pulse.ui.layouts.album;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.MainActivity;
import leematod.pulse.R;
import leematod.pulse.ui.layouts.Layout;

public class AlbumLayout extends Layout {

    public AlbumLayout(Context context) {
        super(context);
        this.sidebar.addTab(R.string.album, R.drawable.album);
        this.sidebar.setActiveTab(0);
    }

    @Nullable
    @Override
    public View sidebarCallback(int pos, @NonNull String name) {
        return new AlbumScreen(this.getContext());
    }

    public void setAlbum(@NonNull String albumId) {
        if (!(this.sidebar.getActiveTabLayout() instanceof AlbumScreen screen)) {
            return;
        }
        MainActivity activity = (MainActivity) this.getContext();
        activity.client.album(albumId).thenAccept(item -> this.post(() -> screen.setAlbum(item)));
    }
}
