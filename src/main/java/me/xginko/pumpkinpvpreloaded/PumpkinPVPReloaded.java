package me.xginko.pumpkinpvpreloaded;

import me.xginko.pumpkinpvpreloaded.commands.pumpkinpvp.PumpkinPVPCommand;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PumpkinPVPReloaded extends JavaPlugin {

    private static PumpkinPVPReloaded instance;
    private static PumpkinPVPConfig config;
    private static Logger logger;

    @Override
    public void onEnable() {
        instance = this;
        logger = getLogger();
        reloadConfiguration();
        getCommand("pumpkinpvp").setExecutor(new PumpkinPVPCommand());
    }

    private void reloadConfiguration() {
        try {
            config = new PumpkinPVPConfig();
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

    public static NamespacedKey getKey(String key) {
        return new NamespacedKey(instance, key);
    }

    public static PumpkinPVPConfig getConfiguration() {
        return config;
    }

    public static Logger getLog() {
        return logger;
    }
}
