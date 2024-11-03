package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands;

import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;

public class DisableSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "disable";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Disable all plugin tasks and listeners.").color(Util.YELLOW);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/pumpkinpvp disable").color(Util.ORANGE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pumpkinpvp.cmd.disable")) return;

        Util.sendMessage(sender, Component.text("Disabling PumpkinPVP...").color(Util.ORANGE));
        PumpkinPVPModule.disableAll();

        Util.sendMessage(sender, Component.text("Disabled all plugin listeners and tasks.").color(Util.GREEN));
        Util.sendMessage(sender, Component.text("You can enable the plugin again using the reload command.").color(Util.YELLOW));
    }
}