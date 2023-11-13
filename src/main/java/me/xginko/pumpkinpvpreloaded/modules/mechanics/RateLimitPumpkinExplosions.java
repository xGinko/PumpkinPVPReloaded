package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.UUID;

public class RateLimitPumpkinExplosions implements PumpkinPVPModule, Listener {

    private final Cache<UUID, Boolean> players_on_cooldown;

    public RateLimitPumpkinExplosions() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.addComment("mechanics.explode-delay.enable", """
                This is meant for servers that allow hacks/cheats to automate pumpkin pvp similar to crystal pvp.\s
                Usually not needed because you can simply turn down explosion power but here just in case.""");
        this.players_on_cooldown = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(config.getInt("mechanics.explode-delay.delay-in-ticks", 4) * 50L))
                .build();
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explode-delay.enable", false);
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
            event.setPrecedingCancelled(true);
        }
    }
}
