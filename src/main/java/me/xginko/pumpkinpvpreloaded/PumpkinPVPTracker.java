package me.xginko.pumpkinpvpreloaded;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.utils.Disableable;
import me.xginko.pumpkinpvpreloaded.utils.Enableable;
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

public final class PumpkinPVPTracker implements Enableable, Disableable, Listener {

    private final @NotNull Cache<Location, Player> pre_pumpkin_explosions;
    private final @NotNull Set<Location> post_pumpkin_explosions;

    PumpkinPVPTracker() {
        Duration cacheDuration = Duration.ofMillis(1500);
        this.pre_pumpkin_explosions = Caffeine.newBuilder().expireAfterWrite(cacheDuration).build();
        this.post_pumpkin_explosions = Collections.newSetFromMap(Caffeine.newBuilder().expireAfterWrite(cacheDuration)
                .<Location, Boolean>build().asMap());
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        this.pre_pumpkin_explosions.put(event.getExplodeLocation(), event.getExploder());
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            this.post_pumpkin_explosions.add(event.getExplodeLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded()) {
            this.post_pumpkin_explosions.add(event.getExplodeLocation());
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