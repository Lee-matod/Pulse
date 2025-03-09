package leematod.pulse.http.innertube.types;

import java.util.List;

public final class Tabs {
    public List<Tab> tabs;

    public static final class Tab {
        public TabRenderer tabRenderer;

        public static final class TabRenderer {
            public Content content;
            public String title;

            public static final class Content {
                public SectionListRenderer sectionListRenderer;
            }
        }
    }
}
