package leematod.pulse.http.innertube.types;

import static leematod.pulse.Utils.map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public final class Runs {
    public List<Run> runs;

    @NonNull
    public String getText() {
        return String.join("", map(this.runs, r -> r.text));
    }

    public static final class Run {
        public String text;
        public @Nullable NavigationEndpoint navigationEndpoint;
    }
}
