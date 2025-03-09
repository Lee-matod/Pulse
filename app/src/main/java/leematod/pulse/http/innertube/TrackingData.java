package leematod.pulse.http.innertube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.function.Function;

public class TrackingData {

    public @Nullable TrackingParams trackingParams;
    public @Nullable Continuation continuation;
    public @Nullable Token token;

    public TrackingData(
            @Nullable TrackingParams trackingParams,
            @Nullable Continuation continuation,
            @Nullable Token token) {
        this.trackingParams = trackingParams;
        this.continuation = continuation;
        this.token = token;
    }

    public record ClickTrackingParams(String value) {}

    public record TrackingParams(String value) {}

    public record Params(String value) {}

    public record Continuation(String value) {}

    public record Token(String value) {}

    public static class Deserializer<T> implements JsonDeserializer<T> {
        private final @NonNull Function<String, T> constructor;

        public Deserializer(@NonNull Function<String, T> constructor) {
            this.constructor = constructor;
        }

        @Override
        public T deserialize(
                @NonNull JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return this.constructor.apply(json.getAsString());
        }
    }
}
