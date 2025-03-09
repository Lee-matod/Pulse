package leematod.pulse.http.innertube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import leematod.pulse.http.Route;

public class Innertube extends Route {
    public static final String BASE = "https://music.youtube.com/youtubei/v1";
    public static final String MASK_MUSIC_RESPONSIVE_LIST_ITEM_RENDERER =
            "musicResponsiveListItemRenderer(trackingParams,flexColumns,fixedColumns,thumbnail,"
                    + "navigationEndpoint)";
    public static final String MASK_MUSIC_TWO_ROW_ITEM_RENDERER =
            "musicTwoRowItemRenderer(thumbnailRenderer,title,subtitle,navigationEndpoint)";
    public static final String MASK_MUSIC_RESPONSIVE_HEADER_RENDERER =
            "musicResponsiveHeaderRenderer(thumbnail,title,subtitle,trackingParams,description,"
                    + "secondSubtitle,facepile)";
    protected final Object body;

    public Innertube(@NonNull String endpoint, @NonNull Object body) {
        super(endpoint);
        this.body = body;
        this.headers.put("Content-Type", "application/json");
        this.headers.put("X-Goog-Api-Key", "AIzaSyAO_FJ2SlqU8Q4STEHLGCilw_Y9_11qcW8");
    }

    public void setMask(@NonNull String mask) {
        this.addHeader("X-Goog-FieldMask", mask);
    }

    @Override
    public Gson engine() {
        return new GsonBuilder()
                .registerTypeAdapter(
                        TrackingData.ClickTrackingParams.class,
                        new TrackingData.Deserializer<>(TrackingData.ClickTrackingParams::new))
                .registerTypeAdapter(
                        TrackingData.TrackingParams.class,
                        new TrackingData.Deserializer<>(TrackingData.TrackingParams::new))
                .registerTypeAdapter(
                        TrackingData.Params.class,
                        new TrackingData.Deserializer<>(TrackingData.Params::new))
                .registerTypeAdapter(
                        TrackingData.Continuation.class,
                        new TrackingData.Deserializer<>(TrackingData.Continuation::new))
                .registerTypeAdapter(
                        TrackingData.Token.class,
                        new TrackingData.Deserializer<>(TrackingData.Token::new))
                .create();
    }

    @Nullable
    @Override
    public Object getBody() {
        return this.body;
    }

    @Nullable
    @Override
    public String getBodyType() {
        return "application/json";
    }

    @NonNull
    @Override
    public String getTarget() {
        return BASE + super.getTarget();
    }
}
