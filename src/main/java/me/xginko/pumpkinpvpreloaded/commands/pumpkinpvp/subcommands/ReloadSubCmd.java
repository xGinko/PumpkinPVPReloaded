package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Reload the plugin configuration.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/pumpkinpvp reload").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pumpkinpvp.cmd.reload")) return;

        KyoriUtil.sendMessage(sender, Component.text("Reloading PumpkinPVP...").color(NamedTextColor.WHITE));
        PumpkinPVPReloaded.getFoliaLib().getImpl().runNextTick(reload -> {
            PumpkinPVPReloaded.getInstance().reloadConfiguration();
            KyoriUtil.sendMessage(sender, Component.text("Reload complete.").color(NamedTextColor.GREEN));
        });
    }
}