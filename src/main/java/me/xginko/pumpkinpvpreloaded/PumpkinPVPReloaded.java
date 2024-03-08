package me.xginko.pumpkinpvpreloaded;

import com.tcoded.folialib.FoliaLib;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.PumpkinPVPCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class PumpkinPVPReloaded extends JavaPlugin {

    private static PumpkinPVPReloaded instance;
    private static PumpkinPVPConfig config;
    private static FoliaLib foliaLib;
    private static BukkitAudiences audiences;
    private static ComponentLogger logger;
    private static Random random;
    private Metrics metrics;

    @Override
    public void onEnable() {
        instance = this;
        audiences = BukkitAudiences.create(this);
        logger = ComponentLogger.logger(getLogger().getName());
        foliaLib = new FoliaLib(this);
        metrics = new Metrics(this, 20296);

        // Fancy enable
        final Style bold_green = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(163,235,30)).build();
        final Style bold_orange = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(254,78,0)).build();
        final Style bold_yellow = Style.style().decorate(TextDecoration.BOLD).color(TextColor.color(242,195,89)).build();
        logger.info(Component.empty());
        logger.info(Component.empty());
        logger.info(Component.text("             ╲╲").style(bold_green));
        logger.info(Component.text("        .╺'```^```'╺.").style(bold_orange));
        logger.info(Component.text("       ╱   ").style(bold_orange)
                .append(Component.text("(\\ __ /)").style(bold_yellow))
                .append(Component.text("  ╲").style(bold_orange)));
        logger.info(Component.text("      │     ").style(bold_orange)
                .append(Component.text("` ╲╱ `").style(bold_yellow))
                .append(Component.text("    │").style(bold_orange)));
        logger.info(Component.text("       ╲    ").style(bold_orange)
                .append(Component.text("\\____/").style(bold_yellow))
                .append(Component.text("   ╱").style(bold_orange)));
        logger.info(Component.text("        `'╺.......╺'`").style(bold_orange));
        logger.info(Component.empty());
        logger.info(Component.text("      PumpkinPVPReloaded").style(bold_green));
        logger.info(Component.text("          by xGinko     ").color(TextColor.color(242,195,89)));
        logger.info(Component.empty());
        logger.info(Component.empty());

        reloadConfiguration();
        getCommand("pumpkinpvp").setExecutor(new PumpkinPVPCommand());
    }

    @Override
    public void onDisable() {
        PumpkinPVPModule.modules.forEach(PumpkinPVPModule::disable);
        PumpkinPVPModule.modules.clear();
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
    }

    public void reloadConfiguration() {
        try {
            config = new PumpkinPVPConfig();
            random = new Random();
            PumpkinPVPModule.reloadModules();
            config.saveConfig();
        } catch (Throwable t) {
            logger.error("Error loading config!", t);
        }
    }

    public static PumpkinPVPReloaded getInstance() {
        return instance;
    }
    public static PumpkinPVPConfig getConfiguration() {
        return config;
    }
    public static FoliaLib getFoliaLib() {
        return foliaLib;
    }
    public static BukkitAudiences getAudiences() {
        return audiences;
    }
    public static ComponentLogger getLog() {
        return logger;
    }
    public static Random getRandom() {
        return random;
    }
}