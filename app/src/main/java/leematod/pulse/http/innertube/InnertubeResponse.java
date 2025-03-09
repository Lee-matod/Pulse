package leematod.pulse.http.innertube;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public class InnertubeResponse<T> {
    public final @Nullable T response;
    public final @NonNull TrackingData data;
    protected final @NonNull BiFunction<T, TrackingData, T> fetcher;
    private @Nullable CompletableFuture<T> nextTask;

    public InnertubeResponse(
            @Nullable T response,
            @Nullable TrackingData.TrackingParams trackingParams,
            @Nullable TrackingData.Continuation continuation,
            @NonNull BiFunction<T, TrackingData, T> fetcher) {
        this.response = response;
        this.data = new TrackingData(trackingParams, continuation, null);
        this.fetcher = fetcher;
    }

    public boolean hasNext() {
        return this.data.continuation != null;
    }

    @Nullable
    public CompletableFuture<T> next() {
        if (this.nextTask != null && !this.nextTask.isDone()) {
            return null;
        }
        if (this.data.continuation == null || this.response == null) {
            return null;
        }
        this.nextTask =
                CompletableFuture.supplyAsync(() -> this.fetcher.apply(this.response, this.data));
        return this.nextTask;
    }
}
