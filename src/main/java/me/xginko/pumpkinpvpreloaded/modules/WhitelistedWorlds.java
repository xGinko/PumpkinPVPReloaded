package me.xginko.pumpkinpvpreloaded.modules;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class WhitelistedWorlds implements PumpkinPVPModule, Listener {

    private final HashSet<String> activeWorlds = new HashSet<>();
    private final boolean blacklistMode;

    protected WhitelistedWorlds() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.activeWorlds.addAll(config.getList("mechanics.whitelisted-worlds.worlds", Bukkit.getWorlds().stream().map(World::getName).toList()));
        this.blacklistMode = config.getBoolean("mechanics.whitelist-world.use-as-blacklist", false);
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.whitelisted-worlds.enable", false);
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