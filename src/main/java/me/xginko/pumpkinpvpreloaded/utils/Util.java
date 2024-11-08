package me.xginko.pumpkinpvpreloaded.utils;

import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Random;

public class Util {

    public static final Random RANDOM;
    public static final TextColor GREEN, ORANGE, YELLOW;
    public static final Style BOLD_GREEN, BOLD_ORANGE, BOLD_YELLOW;

    static {
        RANDOM = new Random();
        GREEN = TextColor.color(163, 235, 30);
        ORANGE = TextColor.color(254, 78, 0);
        YELLOW = TextColor.color(242, 195, 89);
        BOLD_GREEN = Style.style().color(GREEN).decorate(TextDecoration.BOLD).build();
        BOLD_ORANGE = Style.style().color(ORANGE).decorate(TextDecoration.BOLD).build();
        BOLD_YELLOW = Style.style().color(YELLOW).decorate(TextDecoration.BOLD).build();
    }

    public static List<Component> getBootLogo() {
        return ImmutableList.of(
                Component.empty(),
                Component.empty(),
                Component.text("             ╲╲").style(BOLD_GREEN),
                Component.text("        .╺'```^```'╺.").style(BOLD_ORANGE),
                Component.text("       ╱   ").style(BOLD_ORANGE)
                        .append(Component.text("(\\ __ /)").style(BOLD_YELLOW))
                        .append(Component.text("  ╲").style(BOLD_ORANGE)),
                Component.text("      │     ").style(BOLD_ORANGE)
                        .append(Component.text("` ╲╱ `").style(BOLD_YELLOW))
                        .append(Component.text("    │").style(BOLD_ORANGE)),
                Component.text("       ╲    ").style(BOLD_ORANGE)
                        .append(Component.text("\\____/").style(BOLD_YELLOW))
                        .append(Component.text("   ╱").style(BOLD_ORANGE)),
                Component.text("        `'╺.......╺'`").style(BOLD_ORANGE),
                Component.empty(),
                Component.text("      PumpkinPVPReloaded").style(BOLD_GREEN),
                Component.text("          by xGinko     ").color(TextColor.color(242,195,89)),
                Component.empty(),
                Component.empty()
        );
    }

    public static Color fromHexString(String hexString) throws NumberFormatException {
        final String withoutHashTag = hexString.replace("#", "");
        return Color.fromRGB(
                Integer.parseInt(withoutHashTag.substring(0, 2), 16),
                Integer.parseInt(withoutHashTag.substring(2, 4), 16),
                Integer.parseInt(withoutHashTag.substring(4, 6), 16)
        );
    }

    public static void sendMessage(CommandSender sender, Component message) {
        PumpkinPVPReloaded.audiences().sender(sender).sendMessage(message);
    }

    public static boolean hasClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean hasMethod(Class<?> holderClass, String methodName, Class<?>... parameterClasses) {
        try {
            holderClass.getMethod(methodName, parameterClasses);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
}
