package leematod.pulse.http;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Route {
    protected final String endpoint;
    protected final Map<String, String> headers;
    protected final Map<String, String> params;

    public Route(@NonNull String endpoint) {
        this.endpoint = endpoint;
        this.headers = new HashMap<>();
        this.params = new HashMap<>();
    }

    public Gson engine() {
        return new Gson();
    }

    public void addHeader(@NonNull String name, @NonNull String value) {
        this.headers.put(name, value);
    }

    public void addParam(@NonNull String name, @NonNull String value) {
        this.params.put(name, value);
    }

    @Nullable
    public Object getBody() {
        return null;
    }

    @Nullable
    public String getBodyType() {
        return null;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    @NonNull
    public String getTarget() {
        String base = this.endpoint;
        if (!this.params.isEmpty()) {
            ArrayList<String> fmtParams = new ArrayList<>();
            this.params.forEach((name, value) -> fmtParams.add(name + "=" + value));
            base += "?" + String.join("&", fmtParams);
        }
        return base;
    }
}
