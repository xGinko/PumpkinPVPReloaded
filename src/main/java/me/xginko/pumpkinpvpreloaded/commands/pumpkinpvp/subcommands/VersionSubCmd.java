package me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.subcommands;

import io.papermc.paper.plugin.configuration.PluginMeta;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.commands.SubCommand;
import me.xginko.pumpkinpvpreloaded.utils.KyoriUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class VersionSubCmd extends SubCommand {

    @Override
    public String getLabel() {
        return "version";
    }

    @Override
    public TextComponent getDescription() {
        return Component.text("Show the plugin version.").color(NamedTextColor.GRAY);
    }

    @Override
    public TextComponent getSyntax() {
        return Component.text("/pumpkinpvp version").color(NamedTextColor.WHITE);
    }

    @Override
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public void perform(CommandSender sender, String[] args) {
        if (!sender.hasPermission("pumpkinpvp.cmd.version")) return;

        String name, version, website, author;

        try {
            final PluginMeta pluginMeta = PumpkinPVPReloaded.getInstance().getPluginMeta();
            name = pluginMeta.getName();
            version = pluginMeta.getVersion();
            website = pluginMeta.getWebsite();
            author = pluginMeta.getAuthors().get(0);
        } catch (Throwable versionIncompatible) {
            final PluginDescriptionFile pluginYML = PumpkinPVPReloaded.getInstance().getDescription();
            name = pluginYML.getName();
            version = pluginYML.getVersion();
            website = pluginYML.getWebsite();
            author = pluginYML.getAuthors().get(0);
        }

        KyoriUtil.sendMessage(sender, Component.newline()
                .append(
                        Component.text(name + " " + version)
                                .color(NamedTextColor.GOLD)
                                .clickEvent(ClickEvent.openUrl(website))
                )
                .append(Component.text(" by ").color(NamedTextColor.GRAY))
                .append(
                        Component.text(author)
                                .color(NamedTextColor.DARK_AQUA)
                                .clickEvent(ClickEvent.openUrl("https://github.com/xGinko"))
                )
                .append(Component.newline())
        );
    }
}