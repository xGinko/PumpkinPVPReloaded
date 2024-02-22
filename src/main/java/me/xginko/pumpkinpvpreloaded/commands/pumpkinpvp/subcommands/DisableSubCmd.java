package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands;

import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;

public class DisableSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "disable";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Disable all plugin tasks and listeners.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/pumpkinpvp disable").color(NamedTextColor.WHITE);
    }

    @Override
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pumpkinpvp.cmd.disable")) return;

        KyoriUtil.sendMessage(sender, Component.text("Disabling PumpkinPVP...").color(NamedTextColor.RED));
        PumpkinPVPModule.modules.forEach(PumpkinPVPModule::disable);
        PumpkinPVPModule.modules.clear();

        KyoriUtil.sendMessage(sender, Component.text("Disabled all plugin listeners and tasks.").color(NamedTextColor.GREEN));
        KyoriUtil.sendMessage(sender, Component.text("You can enable the plugin again using the reload command.").color(NamedTextColor.GRAY));
    }
}