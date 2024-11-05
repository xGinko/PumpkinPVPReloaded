package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class RateLimitPumpkinExplosions extends PumpkinPVPModule implements Listener {

    private Set<UUID> players_on_cooldown;
    private final long delay_millis;

    public RateLimitPumpkinExplosions() {
        super("mechanics.explode-delay", false,
                "This is meant for servers that allow hacks/cheats to automate pumpkin pvp similar to crystal pvp. \n" +
                "Usually not needed because you can simply turn down explosion power but here just in case.");
        this.delay_millis = config.getInt(configPath + ".delay-in-ticks", 4) * 50L;
    }

    @Override
    public void enable() {
        players_on_cooldown = Collections.newSetFromMap(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMillis(delay_millis)).<UUID, Boolean>build().asMap());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
        if (players_on_cooldown != null) {
            players_on_cooldown.clear();
            players_on_cooldown = null;
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinBlockExplodeEvent event) {
        if (!this.players_on_cooldown.contains(event.getExploder().getUniqueId())) {
            this.players_on_cooldown.add(event.getExploder().getUniqueId());
        } else {
            event.setPrecedingCancelled(true);
            event.setCancelled(true);
        }
    }
}
