package me.xginko.pumpkinpvpreloaded.utils;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class KyoriUtil {

    public static void sendMessage(CommandSender sender, Component message) {
        PumpkinPVPReloaded.getAudiences().sender(sender).sendMessage(message);
    }

    public static void sendActionBar(CommandSender sender, Component message) {
        PumpkinPVPReloaded.getAudiences().sender(sender).sendActionBar(message);
    }
}
