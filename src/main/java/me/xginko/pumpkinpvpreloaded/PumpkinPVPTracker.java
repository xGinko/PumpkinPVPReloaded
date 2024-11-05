package me.xginko.pumpkinpvpreloaded;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.events.PumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PumpkinEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.utils.Disableable;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class PumpkinPVPTracker implements Disableable, Listener {

    private final @NotNull Cache<Location, Player> pre_pumpkin_explosions;
    private final @NotNull Set<Location> post_pumpkin_explosions;

    PumpkinPVPTracker(PumpkinPVPReloaded plugin) {
        Duration cacheDuration = Duration.ofMillis(1500);
        this.pre_pumpkin_explosions = Caffeine.newBuilder().expireAfterWrite(cacheDuration).build();
        this.post_pumpkin_explosions = Collections.newSetFromMap(Caffeine.newBuilder()
                .expireAfterWrite(cacheDuration).<Location, Boolean>build().asMap());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinBlockExplodeEvent event) {
        if (event.getExploder() != null) {
            this.pre_pumpkin_explosions.put(event.getLocation(), event.getExploder());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPostPumpkinExplode(PumpkinBlockExplodeEvent event) {
        if (event.hasExploded()) {
            this.post_pumpkin_explosions.add(event.getLocation());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPostPumpkinHeadExplode(PumpkinEntityExplodeEvent event) {
        if (event.hasExploded()) {
            this.post_pumpkin_explosions.add(event.getLocation());
        }
    }

    public @Nullable Player getPumpkinExploder(Location playerLoc) {
        Player closestExploder = null;
        double smallestDistance = PumpkinPVPReloaded.config().explosion_effect_radius_squared;

        for (Map.Entry<Location, Player> explosion : this.pre_pumpkin_explosions.asMap().entrySet()) {
            if (explosion.getKey().getWorld().getUID().equals(playerLoc.getWorld().getUID())) {
                final double distance = playerLoc.distanceSquared(explosion.getKey());
                if (distance < smallestDistance) {
                    smallestDistance = distance;
                    closestExploder = explosion.getValue();
                }
            }
        }

        return closestExploder;
    }

    public boolean isNearPumpkinExplosion(Location playerLoc) {
        for (Location explosion : this.post_pumpkin_explosions) {
            if (explosion.getWorld().getUID().equals(playerLoc.getWorld().getUID())) {
                if (playerLoc.distanceSquared(explosion) <= PumpkinPVPReloaded.config().explosion_effect_radius_squared) {
                    return true;
                }
            }
        }
        return false;
    }
}