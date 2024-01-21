package me.xginko.pumpkinpvpreloaded;

import com.tcoded.folialib.FoliaLib;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.PumpkinPVPCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public final class PumpkinPVPReloaded extends JavaPlugin {

    private static PumpkinPVPReloaded instance;
    private static PumpkinPVPConfig config;
    private static Logger logger;
    private static Random random;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();

        // Fancy enable
        final Style bold_green = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(163,235,30)).build();
        final Style bold_orange = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(254,78,0)).build();
        final Style bold_yellow = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(242,195,89)).build();
        final TextComponent prefix = Component.text("["+instance.getDescription().getPrefix()+"] ").color(NamedTextColor.WHITE);
        final ConsoleCommandSender console = instance.getServer().getConsoleSender();
        List.of(
                Component.empty(),
                Component.empty(),
                Component.text("             ╲╲").style(bold_green),
                Component.text("        .╺'```^```'╺.").style(bold_orange),
                Component.text("       ╱   ").style(bold_orange).append(Component.text("(\\ __ /)").style(bold_yellow))
                        .append(Component.text("  ╲").style(bold_orange)),
                Component.text("      │     ").style(bold_orange).append(Component.text("` ╲╱ `").style(bold_yellow))
                        .append(Component.text("    │").style(bold_orange)),
                Component.text("       ╲    ").style(bold_orange).append(Component.text("\\____/").style(bold_yellow))
                        .append(Component.text("   ╱").style(bold_orange)),
                Component.text("        `'╺.......╺'`").style(bold_orange),
                Component.text(" "),
                Component.text("      PumpkinPVPReloaded").style(bold_green),
                Component.text("          by xGinko     ").color(TextColor.color(242,195,89)),
                Component.empty(),
                Component.empty()
        ).forEach(line -> console.sendMessage(prefix.append(line)));

        reloadConfiguration();
        getCommand("pumpkinpvp").setExecutor(new PumpkinPVPCommand());
        new Metrics(this, 20296);
    }

    public void reloadConfiguration() {
        try {
            config = new PumpkinPVPConfig();
            random = new Random();
            PumpkinPVPModule.reloadModules();
            config.saveConfig();
        } catch (Exception e) {
            logger.severe("Error loading config! - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    public static PumpkinPVPReloaded getInstance() {
        return instance;
    }
    public static PumpkinPVPConfig getConfiguration() {
        return config;
    }
    public static FoliaLib getFoliaLib() {
        return new FoliaLib(instance);
    }
    public static Logger getLog() {
        return logger;
    }
    public static Random getRandom() {
        return random;
    }
}