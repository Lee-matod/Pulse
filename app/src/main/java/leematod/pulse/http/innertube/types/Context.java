package leematod.pulse.http.innertube.types;

import java.util.Locale;

import leematod.pulse.http.innertube.TrackingData;

public class Context {
    public Client client = new Client();

    public ClickTracking clickTracking;

    public static final class Client {
        public String clientName = "WEB_REMIX";
        public String clientVersion = "1.20241202.01.00";
        public String platform = "DESKTOP";
        public String userAgent =
                "Mozilla/5.0 (X11; Linux x86_64; rv:109.0) Gecko/20100101 Firefox/115.0,gzip(gfe)";
        public String gl = Locale.getDefault().getCountry();
        public String hl = Locale.getDefault().getLanguage();
        public String visitorData = "Cgt6aEctQUV0Q0VZUSjSq8i6BjIKCgJERRIEEgAgWQ%3D%3D";
    }

    public static final class ClickTracking {
        public TrackingData.ClickTrackingParams clickTrackingParams;
    }
}
