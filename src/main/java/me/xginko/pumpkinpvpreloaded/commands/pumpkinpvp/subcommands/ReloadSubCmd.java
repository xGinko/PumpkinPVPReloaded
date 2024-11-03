package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public class ReloadSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "reload";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Reload the plugin configuration.").color(Util.YELLOW);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/pumpkinpvp reload").color(Util.ORANGE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pumpkinpvp.cmd.reload")) return;

        Util.sendMessage(sender, Component.text("Reloading PumpkinPVP...").color(Util.YELLOW));

        PumpkinPVPReloaded.scheduling().asyncScheduler().run(reload -> {
            PumpkinPVPReloaded.getInstance().reloadConfiguration();
            Util.sendMessage(sender, Component.text("Reload complete.").color(Util.GREEN));
        });
    }
}