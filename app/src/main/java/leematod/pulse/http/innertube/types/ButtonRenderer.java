package leematod.pulse.http.innertube.types;

public final class ButtonRenderer {
    public ButtonRendererContent buttonRenderer;

    public static final class ButtonRendererContent {
        public NavigationEndpoint navigationEndpoint;
        public Runs text;
    }
}
