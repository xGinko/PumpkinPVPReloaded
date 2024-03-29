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
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.UUID;

public class RateLimitPumpkinExplosions implements PumpkinPVPModule, Listener {

    private final @Nullable Cache<UUID, Boolean> players_on_cooldown;
    private final long delay_millis;

    public RateLimitPumpkinExplosions() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.master().addComment(configPath() + ".enable",
                "This is meant for servers that allow hacks/cheats to automate pumpkin pvp similar to crystal pvp. \n" +
                "Usually not needed because you can simply turn down explosion power but here just in case.");
        this.delay_millis = config.getInt(configPath() + ".delay-in-ticks", 4) * 50L;
        this.players_on_cooldown = delay_millis > 0 ? Caffeine.newBuilder().expireAfterWrite(Duration.ofMillis(delay_millis)).build() : null;
    }

    @Override
    public String configPath() {
        return "mechanics.explode-delay";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath() + ".enable", false)
                && delay_millis > 0;
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
        if (this.players_on_cooldown.getIfPresent(event.getExploder().getUniqueId()) == null) {
            this.players_on_cooldown.put(event.getExploder().getUniqueId(), true);
        } else {
            event.setPrecedingCancelled(true);
            event.setCancelled(true);
        }
    }
}
