package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;

public class EnablePerWorld implements PumpkinPVPModule, Listener {

    private final HashSet<String> activeWorlds = new HashSet<>();
    private final boolean blacklistMode;

    public EnablePerWorld() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.addComment("mechanics.enabled-worlds.enable",
                "Add the names of the worlds you want this plugins features to be enabled in.");
        this.activeWorlds.addAll(config.getList("mechanics.enabled-worlds.worlds",
                List.of("world", "world_nether", "world_the_end")));
        this.blacklistMode = config.getBoolean("mechanics.enabled-worlds.use-as-blacklist", false,
                "Make it so that the plugin's features are disabled in the listed worlds.");
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.enabled-worlds.enable", false);
    }

    @Override
    public void enable() {
        PumpkinPVPReloaded plugin = PumpkinPVPReloaded.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        event.setCancelled(blacklistMode == activeWorlds.contains(event.getPumpkin().getWorld().getName()));
    }
}