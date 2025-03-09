package leematod.pulse.http.innertube.types;

import androidx.annotation.NonNull;

public final class Thumbnail {
    public String url;
    public int height;
    public int width;

    @NonNull
    public String withDimensions(int width, int height) {
        String[] parts = this.url.split("=");
        if (this.url.startsWith("https://lh3.googleusercontent.com")) {
            return parts[0] + "=w" + width + "-h" + height;
        } else if (this.url.startsWith("https://yt3.ggpht.com")) {
            return parts[0] + "-s" + Math.max(width, height);
        }
        return this.url;
    }

    @NonNull
    public String banner() {
        return this.banner(1440, 600);
    }

    @NonNull
    public String banner(int width, int height) {
        return this.withDimensions(width, height) + "-p";
    }

    @NonNull
    public String profile() {
        return this.withDimensions(512, 512);
    }
}
