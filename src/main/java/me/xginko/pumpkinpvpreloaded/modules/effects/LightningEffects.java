package me.xginko.pumpkinpvpreloaded.modules.effects;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import org.bukkit.Location;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class LightningEffects extends PumpkinPVPModule implements Listener {

    private static final boolean HAS_SETFLASHCOUNT = Util.hasMethod(LightningStrike.class, "setFlashCount", int.class);

    private final double probability;
    private final int spawn_amount;
    private final boolean deal_damage;
    private int flash_count;

    public LightningEffects() {
        super("pumpkin-explosion.lightning-effects", true, 
                "Will strike the closest player with lightning.");
        this.deal_damage = config.getBoolean(configPath + ".deal-damage", true);
        this.spawn_amount = Math.max(config.getInt(configPath + ".lightning-strikes", 2,
                "Amount of times to strike."), 1);
        if (HAS_SETFLASHCOUNT) { // Don't make the config option appear if it's not supported by that version
            this.flash_count = Math.max(config.getInt(configPath + ".lightning-flash-count", 2,
                    "Amount of times to flash after strike."), 0);
        }
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
        if (event.hasExploded() && (probability >= 1 || Util.RANDOM.nextDouble() <= probability)) {
            strikeLightning(event.getExploder().getUniqueId(), event.getExplodeLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || Util.RANDOM.nextDouble() <= probability)) {
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

        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(playerLoc).run(() -> spawnLightning(playerLoc));
        } else {
            spawnLightning(playerLoc);
        }
    }

    private void spawnLightning(Location location) {
        for (int i = 0; i < spawn_amount; i++) {
            LightningStrike lightning;

            if (deal_damage) {
                lightning = location.getWorld().strikeLightning(location);
            } else {
                lightning = location.getWorld().strikeLightningEffect(location);
            }

            if (HAS_SETFLASHCOUNT) {
                lightning.setFlashCount(flash_count);
            }
        }
    }
}