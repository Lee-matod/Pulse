package leematod.pulse.ui.layouts.artist;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.R;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.layouts.Layout;

public class ArtistLayout extends Layout {
    protected @Nullable String artistId;

    public ArtistLayout(Context context) {
        super(context);
        this.sidebar.addTab(R.string.overview, R.drawable.sparkles);
        this.sidebar.addTab(R.string.songs, R.drawable.music_library);
        this.sidebar.addTab(R.string.albums, R.drawable.album);
        this.sidebar.addTab(R.string.singles, R.drawable.album);
        this.sidebar.setActiveTab(0);
    }

    public void setArtist(@NonNull String artistId) {
        this.artistId = artistId;
        if (this.sidebar.getActiveTabLayout() instanceof CompatibleScreen layout) {
            layout.setArtist(artistId);
        }
    }

    @Override
    public void setPalette(@NonNull ColorPalette palette) {
        super.setPalette(palette);
    }

    @Nullable
    @Override
    public View sidebarCallback(int pos, @NonNull String name) {
        return switch (pos) {
            case 0 -> new ArtistScreen(this.getContext());
            default -> null;
        };
    }

    public interface CompatibleScreen {
        void setArtist(@NonNull String artistId);
    }
}
