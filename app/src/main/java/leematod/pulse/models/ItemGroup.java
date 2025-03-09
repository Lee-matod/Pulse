package leematod.pulse.models;

import static leematod.pulse.Utils.find;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.types.NavigationEndpoint;

import java.util.ArrayList;
import java.util.List;

public class ItemGroup {
    public final @NonNull String name;
    public final @NonNull List<Item<?>> items;
    public final @Nullable NavigationEndpoint.Browse continuation;

    public ItemGroup(@NonNull String name) {
        this(name, new ArrayList<>());
    }

    public ItemGroup(@NonNull String name, @NonNull List<Item<?>> items) {
        this(name, items, null);
    }

    public ItemGroup(@NonNull String name, @Nullable NavigationEndpoint.Browse continuation) {
        this(name, new ArrayList<>(), continuation);
    }

    public ItemGroup(
            @NonNull String name,
            @NonNull List<Item<?>> items,
            @Nullable NavigationEndpoint.Browse continuation) {
        this.name = name;
        this.items = items;
        this.continuation = continuation;
    }

    @Nullable
    public Item.Type getItemType() {
        if (this.items.isEmpty()) {
            return null;
        }

        Item.Type first = this.items.get(0).getType();
        if (find(this.items, i -> i.getType() != first) != null) {
            return null;
        }
        return first;
    }
}
