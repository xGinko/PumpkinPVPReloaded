package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
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

public class LightningEffects implements PumpkinPVPModule, Listener {

    private final @Nullable ServerImplementation scheduler;
    private final boolean is_folia, deal_damage;
    private final int spawn_amount, flash_count;
    private final double probability, expl_effect_radius;

    public LightningEffects() {
        shouldEnable();
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.is_folia = foliaLib.isFolia();
        this.scheduler = is_folia ? foliaLib.getImpl() : null;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.expl_effect_radius = config.explosion_effect_radius_squared;
        config.master().addComment("pumpkin-explosion.lightning-effects",
                "Will strike the closest player with lightning.");
        this.deal_damage = config.getBoolean("pumpkin-explosion.lightning-effects.deal-damage", true);
        this.spawn_amount = Math.max(config.getInt("pumpkin-explosion.lightning-effects.lightning-strikes", 2,
                "Amount of times to strike."), 1);
        this.flash_count = Math.max(config.getInt("pumpkin-explosion.lightning-effects.lightning-flash-count", 2,
                "Amount of times to flash after strike."), 0);
        this.probability = config.getDouble("pumpkin-explosion.lightning-effects.lightning-chance", 0.1,
                "Percentage as double: 100% = 1.0");
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-explosion.lightning-effects.enable", false)
                && probability > 0;
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

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || PumpkinPVPReloaded.getRandom().nextDouble() <= probability)) {
            strikeLightning(event.getExploder().getUniqueId(), event.getExplodeLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || PumpkinPVPReloaded.getRandom().nextDouble() <= probability)) {
            strikeLightning(event.getKiller() != null ? event.getKiller().getUniqueId() : null, event.getExplodeLocation());
        }
    }

    private void strikeLightning(@Nullable final UUID exploder, final Location explosionLoc) {
        Player closestPlayer = null;
        double smallestDistance = expl_effect_radius;
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

        if (is_folia) {
            scheduler.runAtLocation(playerLoc, strike -> {
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