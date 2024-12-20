package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp;

import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.DisableSubCmd;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.ReloadSubCmd;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands.VersionSubCmd;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PumpkinPVPCommand implements TabCompleter, CommandExecutor {

    private final List<SubCommand> subCommands;
    private final List<String> tabCompleter;

    public PumpkinPVPCommand() {
        this.subCommands = ImmutableList.of(new ReloadSubCmd(), new VersionSubCmd(), new DisableSubCmd());
        this.tabCompleter = subCommands.stream().map(SubCommand::getLabel)
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return args.length == 1 ? tabCompleter : Collections.emptyList();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sendCommandOverview(sender);
            return true;
        }

        for (final SubCommand subCommand : subCommands) {
            if (args[0].equalsIgnoreCase(subCommand.getLabel())) {
                subCommand.perform(sender, args);
                return true;
            }
        }

        sendCommandOverview(sender);
        return true;
    }

    private void sendCommandOverview(CommandSender sender) {
        if (!sender.hasPermission("pumpkinpvp.cmd.*")) return;
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GREEN));
        Util.sendMessage(sender, Component.text(" PumpkinPVP Commands").color(Util.ORANGE));
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GREEN));
        subCommands.forEach(subCommand -> Util.sendMessage(sender,
                subCommand.getSyntax().append(Component.text(" - ").color(Util.GREEN)).append(subCommand.getDescription())));
        Util.sendMessage(sender, Component.text("-----------------------------------------------------").color(Util.GREEN));
    }
}