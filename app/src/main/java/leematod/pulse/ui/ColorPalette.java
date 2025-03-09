package leematod.pulse.ui;


import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

// TODO: Add more themes
public final class ColorPalette {
    public static ColorPalette current = ColorPalette.pureBlack();
    public final int primary;
    public final int secondary;
    public final int background;
    public final int accent;
    public final int onAccent;
    public final int text;
    public final int textSecondary;
    public final int textDisabled;
    public final boolean isDark;

    public ColorPalette(
            int primary,
            int secondary,
            int background,
            int accent,
            int onAccent,
            int text,
            int textSecondary,
            int textDisabled,
            boolean isDark) {
        this.primary = primary;
        this.secondary = secondary;
        this.background = background;
        this.accent = accent;
        this.onAccent = onAccent;
        this.text = text;
        this.textSecondary = textSecondary;
        this.textDisabled = textDisabled;
        this.isDark = isDark;
    }

    @NonNull
    @Contract(" -> new")
    public static ColorPalette pureBlack() {
        return new ColorPalette(
                0xff38a9e6,
                0xff187795,
                0xff000000,
                0xff202020,
                0xffffffff,
                0xFFeff0f4,
                0xFF898688,
                0xFF636061,
                true);
    }
}
