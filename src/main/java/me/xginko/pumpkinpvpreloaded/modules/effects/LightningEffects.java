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

import java.util.Random;
import java.util.UUID;

public class LightningEffects implements PumpkinPVPModule, Listener {

    private final ServerImplementation scheduler;
    private final boolean isFolia, deal_damage;
    private final int spawn_amount, flashcount;
    private final double probability;

    public LightningEffects() {
        shouldEnable();
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.addComment("pumpkin-explosion.lightning-effects", "Will strike the closest player with lightning.");
        this.deal_damage = config.getBoolean("pumpkin-explosion.lightning-effects.deal-damage", true);
        this.spawn_amount = config.getInt("pumpkin-explosion.lightning-effects.lightning-strikes", 2, "Amount of times to strike.");
        this.flashcount = config.getInt("pumpkin-explosion.lightning-effects.lightning-flash-count", 2, "Amount of times to flash after strike.");
        this.probability = config.getDouble("pumpkin-explosion.lightning-effects.lightning-chance", 0.1, "Percentage as double: 100% = 1.0");
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-explosion.lightning-effects.enable", false);
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
        if (event.hasExploded() && (probability >= 1 || new Random().nextDouble() <= probability)) {
            strikeLightning(event.getExploder().getUniqueId(), event.getExplodeLocation());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded() && (probability >= 1 || new Random().nextDouble() <= probability)) {
            strikeLightning(event.getKiller() != null ? event.getKiller().getUniqueId() : null, event.getExplodeLocation());
        }
    }

    private void strikeLightning(@Nullable final UUID exploder, final Location explosionLoc) {
        Player closestPlayer = null;
        double distance = 100;
        for (Player player : explosionLoc.getNearbyPlayers(6, 6, 6)) {
            if (exploder != null && player.getUniqueId().equals(exploder)) continue;
            double currentDistance = explosionLoc.distance(player.getLocation());
            if (currentDistance < distance) {
                closestPlayer = player;
                distance = currentDistance;
            }
        }

        if (closestPlayer == null) return;
        final Location playerLoc = closestPlayer.getLocation();
        final World world = playerLoc.getWorld();

        if (isFolia) {
            scheduler.runAtEntity(closestPlayer, strike -> {
                for (int i = 0; i < spawn_amount; i++) {
                    (deal_damage ? world.strikeLightning(playerLoc) : world.strikeLightningEffect(playerLoc)).setFlashCount(flashcount);
                }
            });
        } else {
            for (int i = 0; i < spawn_amount; i++) {
                (deal_damage ? world.strikeLightning(playerLoc) : world.strikeLightningEffect(playerLoc)).setFlashCount(flashcount);
            }
        }
    }
}