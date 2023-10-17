package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp;

import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.DisableSubCmd;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.ReloadSubCmd;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.VersionSubCmd;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PumpkinPVPCommand implements TabCompleter, CommandExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>(3);
    private final List<String> tabCompleter = new ArrayList<>(3);

    public PumpkinPVPCommand() {
        subCommands.add(new ReloadSubCmd());
        subCommands.add(new VersionSubCmd());
        subCommands.add(new DisableSubCmd());
        subCommands.forEach(subCommand -> tabCompleter.add(subCommand.getLabel()));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return args.length == 1 ? tabCompleter : null;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length > 0) {
            boolean cmdExists = false;
            for (SubCommand subCommand : subCommands) {
                if (args[0].equalsIgnoreCase(subCommand.getLabel())) {
                    subCommand.perform(sender, args);
                    cmdExists = true;
                    break;
                }
            }
            if (!cmdExists) sendCommandOverview(sender);
        } else {
            sendCommandOverview(sender);
        }
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        if (!sender.hasPermission("pumpkinpvp.cmd.*")) return;
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        sender.sendMessage(Component.text("PumpkinPVP Commands").color(NamedTextColor.WHITE));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
        subCommands.forEach(subCommand -> sender.sendMessage(
                subCommand.getSyntax().append(Component.text(" - ").color(NamedTextColor.DARK_GRAY)).append(subCommand.getDescription())));
        sender.sendMessage(Component.text("-----------------------------------------------------").color(NamedTextColor.GRAY));
    }
}