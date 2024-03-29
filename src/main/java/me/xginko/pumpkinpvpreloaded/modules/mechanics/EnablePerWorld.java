package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;

public class EnablePerWorld implements PumpkinPVPModule, Listener {

    private final @NotNull HashSet<String> active_worlds;
    private final boolean blacklist_mode;

    public EnablePerWorld() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.master().addComment(configPath() + ".enable",
                "Add the names of the worlds you want this plugins features to be enabled in.");
        this.blacklist_mode = config.getBoolean(configPath() + ".use-as-blacklist", false,
                "Make it so that the plugin's features are disabled in the listed worlds.");
        this.active_worlds = new HashSet<>(config.getList(configPath() + ".worlds",
                Arrays.asList("world", "world_nether", "world_the_end")));
    }

    @Override
    public String configPath() {
        return "mechanics.enabled-worlds";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath() + ".enable", false);
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
        event.setCancelled(blacklist_mode == active_worlds.contains(event.getPumpkin().getWorld().getName()));
    }
}