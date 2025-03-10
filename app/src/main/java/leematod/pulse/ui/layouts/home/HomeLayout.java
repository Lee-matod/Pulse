package leematod.pulse.ui.layouts.home;

import static leematod.pulse.Utils.ensureId;
import static leematod.pulse.Utils.pixels;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.R;
import leematod.pulse.ui.ColorPalette;
import leematod.pulse.ui.Styleable;
import leematod.pulse.ui.components.PrimaryTextView;
import leematod.pulse.ui.components.Sidebar;
import leematod.pulse.ui.layouts.Layout;

public class HomeLayout extends Layout {
    public static final int TITLE_SIZE = 48;

    public HomeLayout(@NonNull Context context) {
        super(context);

        this.sidebar.addTab(R.string.home, R.drawable.home);
        this.sidebar.addTab(R.string.songs, R.drawable.music_library);
        this.sidebar.addTab(R.string.playlists, R.drawable.playlist);
        this.sidebar.addTab(R.string.artists, R.drawable.person);
        this.sidebar.addTab(R.string.albums, R.drawable.album);
        this.sidebar.setIcon(R.drawable.settings);

        this.sidebar.setActiveTab(0);
    }

    @Nullable
    @Override
    public View sidebarCallback(int pos, @NonNull String name) {
        ViewGroup view =
                switch (pos) {
                    case 0 -> new HomeScreen(this.getContext());
                    case 1 -> new SongsScreen(this.getContext());
                    case 2 -> new PlaylistsScreen(this.getContext());
                    case 3 -> new ArtistsScreen(this.getContext());
                    case 4 -> new AlbumsScreen(this.getContext());
                    default -> null;
                };
        if (view == null) {
            return null;
        }
        return this.withTitle(view, name);
    }

    protected ViewGroup withTitle(@NonNull ViewGroup view, @NonNull String name) {
        TextView title = ensureId(new PrimaryTextView(this.getContext()));
        title.setText(name);
        title.setTextSize(TITLE_SIZE);
        title.setPaddingRelative(0, pixels(Sidebar.WIDTH / 4), 0, 0);
        view.addView(title, 0);
        return view;
    }
}
