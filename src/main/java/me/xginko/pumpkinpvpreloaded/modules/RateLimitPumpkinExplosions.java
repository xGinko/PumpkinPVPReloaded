package me.xginko.pumpkinpvpreloaded.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.UUID;

public class RateLimitPumpkinExplosions implements PumpkinPVPModule, Listener {

    private final Cache<UUID, Boolean> players_on_cooldown;

    protected RateLimitPumpkinExplosions() {
        shouldEnable();
        this.players_on_cooldown = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(PumpkinPVPReloaded.getConfiguration().getInt("per-player-explode-delay.delay-in-ticks", 4) * 50L))
                .build();
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("per-player-explode-delay.enable", false);
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
        final UUID exploderUniqueId = event.getExploder().getUniqueId();
        if (this.players_on_cooldown.getIfPresent(exploderUniqueId) == null) {
            this.players_on_cooldown.put(exploderUniqueId, true);
        } else {
            event.setCancelled(true);
        }
    }
}
