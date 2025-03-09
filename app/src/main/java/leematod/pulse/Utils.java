package leematod.pulse;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class Utils {
    public static Resources resources;

    private Utils() {}

    @NonNull
    @Contract("_ -> param1")
    @CanIgnoreReturnValue
    public static <V extends View> V ensureId(@NonNull V view) {
        if (view.getId() == View.NO_ID) {
            view.setId(View.generateViewId());
        }
        return view;
    }

    @NonNull
    public static <T, U> List<U> map(@NonNull Collection<T> iter, Function<T, U> func) {
        return iter.stream().map(func).collect(Collectors.toList());
    }

    @NonNull
    public static <T> List<T> filter(@NonNull Collection<T> iter) {
        return filter(iter, null);
    }

    @NonNull
    public static <T> List<T> filter(
            @NonNull Collection<T> iter, @Nullable Function<T, Boolean> predicate) {
        if (predicate == null) {
            predicate = Objects::nonNull;
        }
        return iter.stream().filter(predicate::apply).collect(Collectors.toList());
    }

    @Nullable
    public static <T> T find(@NonNull Collection<T> iter) {
        return find(iter, null);
    }

    @Nullable
    public static <T> T find(
            @NonNull Collection<T> iter, @Nullable Function<T, Boolean> predicate) {
        List<T> results = filter(iter, predicate);
        try {
            return results.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    @Nullable
    public static <T> T nullsafe(@NonNull Supplier<T> func) {
        try {
            return func.get();
        } catch (NullPointerException e) {
            return null;
        }
    }

    @NonNull
    public static <T> T nullsafe(@NonNull Supplier<T> func, @NonNull T def) {
        try {
            return func.get();
        } catch (NullPointerException e) {
            return def;
        }
    }

    public static int pixels(int dip) {
        return (int)
                TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
    }
}
