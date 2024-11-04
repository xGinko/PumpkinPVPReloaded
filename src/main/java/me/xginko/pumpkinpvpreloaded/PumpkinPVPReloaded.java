package me.xginko.pumpkinpvpreloaded;

import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.PumpkinPVPCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.MorePaperLib;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.util.Objects;

public final class PumpkinPVPReloaded extends JavaPlugin {

    private static PumpkinPVPReloaded instance;
    private static PumpkinPVPConfig config;
    private static PumpkinPVPTracker tracker;
    private static GracefulScheduling scheduling;
    private static BukkitAudiences audiences;
    private static ComponentLogger logger;
    private static Metrics metrics;
    private static boolean isServerFolia;

    @Override
    public void onEnable() {
        instance = this;
        audiences = BukkitAudiences.create(instance);
        logger = ComponentLogger.logger(getLogger().getName());
        scheduling = new MorePaperLib(instance).scheduling();
        metrics = new Metrics(instance, 20296);
        isServerFolia = Util.hasClass("io.papermc.paper.threadedregions.RegionizedServer");
        tracker = new PumpkinPVPTracker(instance);

        logger.info(Component.empty());
        Util.getPumpkin().forEach(logger::info);
        logger.info(Component.text("      PumpkinPVPReloaded").style(Util.BOLD_GREEN));
        logger.info(Component.text("          by xGinko     ").color(TextColor.color(242,195,89)));
        logger.info(Component.empty());
        logger.info(Component.empty());

        reloadConfiguration();

        Objects.requireNonNull(getCommand("pumpkinpvp"), "Command isn't defined in the plugin.yml!")
                .setExecutor(new PumpkinPVPCommand());
    }

    @Override
    public void onDisable() {
        PumpkinPVPModule.disableAll();
        if (tracker != null) {
            tracker.disable();
            tracker = null;
        }
        if (scheduling != null) {
            scheduling.cancelGlobalTasks();
            scheduling = null;
        }
        if (audiences != null) {
            audiences.close();
            audiences = null;
        }
        if (metrics != null) {
            metrics.shutdown();
            metrics = null;
        }
        Util.RANDOM = null;
        logger = null;
        instance = null;
    }

    public void reloadConfiguration() {
        try {
            config = new PumpkinPVPConfig();
            PumpkinPVPModule.reloadModules();
            config.saveConfig();
        } catch (Throwable t) {
            logger.error("Error loading config!", t);
        }
    }

    public static PumpkinPVPReloaded getInstance() {
        return instance;
    }

    public static PumpkinPVPTracker getTracker() {
        return tracker;
    }

    public static PumpkinPVPConfig config() {
        return config;
    }

    public static GracefulScheduling scheduling() {
        return scheduling;
    }

    public static BukkitAudiences audiences() {
        return audiences;
    }

    public static ComponentLogger logger() {
        return logger;
    }

    public static boolean isServerFolia() {
        return isServerFolia;
    }
}