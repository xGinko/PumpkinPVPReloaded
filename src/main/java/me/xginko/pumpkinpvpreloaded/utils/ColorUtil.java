package me.xginko.pumpkinpvpreloaded.utils;

import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;

public class ColorUtil {

    public static final TextColor GREEN = TextColor.color(163, 235, 30);
    public static final Style BOLD_GREEN = Style.style().color(GREEN).decorate(TextDecoration.BOLD).build();
    public static final TextColor ORANGE = TextColor.color(254, 78, 0);
    public static final Style BOLD_ORANGE = Style.style().color(ORANGE).decorate(TextDecoration.BOLD).build();
    public static final TextColor YELLOW = TextColor.color(242, 195, 89);
    public static final Style BOLD_YELLOW = Style.style().color(YELLOW).decorate(TextDecoration.BOLD).build();

    public static Color fromHexString(String hexString) throws NumberFormatException {
        final String withoutHashTag = hexString.replace("#", "");
        return Color.fromRGB(
                Integer.parseInt(withoutHashTag.substring(0, 2), 16),
                Integer.parseInt(withoutHashTag.substring(2, 4), 16),
                Integer.parseInt(withoutHashTag.substring(4, 6), 16)
        );
    }
}
