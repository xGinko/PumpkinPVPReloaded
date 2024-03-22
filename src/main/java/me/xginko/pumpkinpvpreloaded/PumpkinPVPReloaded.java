package me.xginko.pumpkinpvpreloaded;

import com.tcoded.folialib.FoliaLib;
import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.PumpkinPVPCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.ColorUtil;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
        logger.info(Component.empty());
        logger.info(Component.empty());
        logger.info(Component.text("             ╲╲").style(ColorUtil.BOLD_GREEN));
        logger.info(Component.text("        .╺'```^```'╺.").style(ColorUtil.BOLD_ORANGE));
        logger.info(Component.text("       ╱   ").style(ColorUtil.BOLD_ORANGE)
                .append(Component.text("(\\ __ /)").style(ColorUtil.BOLD_YELLOW))
                .append(Component.text("  ╲").style(ColorUtil.BOLD_ORANGE)));
        logger.info(Component.text("      │     ").style(ColorUtil.BOLD_ORANGE)
                .append(Component.text("` ╲╱ `").style(ColorUtil.BOLD_YELLOW))
                .append(Component.text("    │").style(ColorUtil.BOLD_ORANGE)));
        logger.info(Component.text("       ╲    ").style(ColorUtil.BOLD_ORANGE)
                .append(Component.text("\\____/").style(ColorUtil.BOLD_YELLOW))
                .append(Component.text("   ╱").style(ColorUtil.BOLD_ORANGE)));
        logger.info(Component.text("        `'╺.......╺'`").style(ColorUtil.BOLD_ORANGE));
        logger.info(Component.empty());
        logger.info(Component.text("      PumpkinPVPReloaded").style(ColorUtil.BOLD_GREEN));
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
        if (foliaLib != null) {
            foliaLib.getImpl().cancelAllTasks();
            foliaLib = null;
        }
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        random = null;
        logger = null;
        instance = null;
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
    public static ComponentLogger getPrefixedLogger() {
        return logger;
    }
    public static Random getRandom() {
        return random;
    }
}