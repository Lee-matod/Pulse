package leematod.pulse.http.innertube.types;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import leematod.pulse.http.innertube.TrackingData;
import leematod.pulse.models.Item;

import java.util.List;

public final class MusicResponsiveHeaderRenderer {
    public ThumbnailRenderer thumbnail;
    public Runs title;
    public Runs subtitle;
    public Runs secondSubtitle;
    public HeaderDescription description;
    public @Nullable HeaderFacepile facepile;
    public @Nullable Runs straplineTextOne;
    public @Nullable ThumbnailRenderer straplineThumbnailRenderer;

    public <I extends Item<NavigationEndpoint.Browse>> I apply(
            @NonNull Item.DefaultConstructor<I, NavigationEndpoint.Browse> constructor) {
        String name = this.title.getText();
        Thumbnail thumbnail = this.thumbnail.get();
        return constructor.init(name, thumbnail, null, null);
    }

    public static final class HeaderDescription {
        public DescriptionShelfRenderer musicDescriptionShelfRenderer;

        public static final class DescriptionShelfRenderer {
            public Runs description;
        }
    }

    public static final class HeaderFacepile {
        public AvatarStackModel avatarStackViewModel;

        public static final class AvatarStackModel {
            public Text text;
            public @Nullable List<Avatar> avatars;
            public RendererContext rendererContext;

            public static final class RendererContext {
                public @Nullable RendererOnTap onTap;

                public static final class RendererOnTap {
                    public InnertubeCommand innertubeCommand;

                    public static final class InnertubeCommand {
                        public TrackingData.ClickTrackingParams clickTrackingParams;
                        public NavigationEndpoint.Browse browseEndpoint;
                    }
                }
            }

            public static final class Text {
                public String content;
            }

            public static final class Avatar {
                public AvatarViewModel avatarViewModel;

                public static final class AvatarViewModel {
                    public AvatarViewModelImage image;

                    public static final class AvatarViewModelImage {
                        public List<Source> sources;

                        public static final class Source {
                            public String url;
                        }
                    }
                }
            }
        }
    }
}
