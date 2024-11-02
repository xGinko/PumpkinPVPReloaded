package me.xginko.pumpkinpvpreloaded.modules.effects;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LightningEffects extends PumpkinPVPModule implements Listener {

    private final double probability;
    private final int spawn_amount, flash_count;
    private final boolean deal_damage;

    public LightningEffects() {
        super("pumpkin-explosion.lightning-effects", true, 
                "Will strike the closest player with lightning.");
        this.deal_damage = config.getBoolean(configPath + ".deal-damage", true);
        this.spawn_amount = Math.max(config.getInt(configPath + ".lightning-strikes", 2,
                "Amount of times to strike."), 1);
        this.flash_count = Math.max(config.getInt(configPath + ".lightning-flash-count", 2,
                "Amount of times to flash after strike."), 0);
        this.probability = config.getDouble(configPath + ".lightning-chance", 0.1,
                "Percentage as double: 100% = 1.0");
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || PumpkinPVPReloaded.random().nextDouble() <= probability)) {
            strikeLightning(event.getExploder().getUniqueId(), event.getExplodeLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || PumpkinPVPReloaded.random().nextDouble() <= probability)) {
            strikeLightning(event.getKiller() != null ? event.getKiller().getUniqueId() : null, event.getExplodeLocation());
        }
    }

    private void strikeLightning(@Nullable final UUID exploder, final Location explosionLoc) {
        Player closestPlayer = null;
        double smallestDistance = config.explosion_effect_radius_squared;
        for (Player player : explosionLoc.getNearbyPlayers(6, 6, 6)) {
            if (exploder != null && player.getUniqueId().equals(exploder)) continue;
            double currentDistance = explosionLoc.distanceSquared(player.getLocation());
            if (currentDistance < smallestDistance) {
                closestPlayer = player;
                smallestDistance = currentDistance;
            }
        }

        if (closestPlayer == null) return;
        final Location playerLoc = closestPlayer.getLocation();
        final World world = playerLoc.getWorld();

        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(playerLoc).run(() -> {
                for (int i = 0; i < spawn_amount; i++) {
                    (deal_damage ? world.strikeLightning(playerLoc) : world.strikeLightningEffect(playerLoc)).setFlashCount(flash_count);
                }
            });
        } else {
            for (int i = 0; i < spawn_amount; i++) {
                (deal_damage ? world.strikeLightning(playerLoc) : world.strikeLightningEffect(playerLoc)).setFlashCount(flash_count);
            }
        }
    }
}