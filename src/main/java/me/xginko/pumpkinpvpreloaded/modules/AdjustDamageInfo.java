package me.xginko.pumpkinpvpreloaded.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

public class AdjustDamageInfo implements PumpkinPVPModule, Listener {

    private final Cache<Location, Player> pumpkinExploders;
    private final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> emptyDamageModifierMap;

    protected AdjustDamageInfo() {
        this.pumpkinExploders = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(1)).build();
        this.emptyDamageModifierMap = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-deaths.attempt-to-correct-death-details", true,
                "Tries to fill in the blanks so the game can roughly tell who killed who.");
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

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        this.pumpkinExploders.put(event.getExplodeLocation(), event.getExploder());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) return;

        final Player damagedPlayer = (Player) event.getEntity();
        final Player exploder = getClosestPumpkinExploder(damagedPlayer.getLocation());
        if (exploder == null) return;

        final EntityDamageByEntityEvent damageByExploder = new EntityDamageByEntityEvent(
                exploder,
                damagedPlayer,
                EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                emptyDamageModifierMap
        );

        if (!damageByExploder.callEvent()) {
            event.setCancelled(true);
            return;
        }

        damagedPlayer.setLastDamageCause(damageByExploder);
        damagedPlayer.setKiller(exploder);
    }

    private @Nullable Player getClosestPumpkinExploder(Location playerLoc) {
        double smallestDistance = 100.0; // 10 Blocks squared
        Player closestExploder = null;

        for (Map.Entry<Location, Player> explosion : this.pumpkinExploders.asMap().entrySet()) {
            if (explosion.getKey().getWorld().getUID().equals(playerLoc.getWorld().getUID())) {
                final double currentDistance = playerLoc.distanceSquared(explosion.getKey());
                if (currentDistance < smallestDistance) {
                    smallestDistance = currentDistance;
                    closestExploder = explosion.getValue();
                }
            }
        }

        return closestExploder;
    }
}