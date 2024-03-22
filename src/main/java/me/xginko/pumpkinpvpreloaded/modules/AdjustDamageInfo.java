package me.xginko.pumpkinpvpreloaded.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;

@SuppressWarnings({"deprecation", "removal", "UnstableApiUsage"})
public class AdjustDamageInfo implements PumpkinPVPModule, Listener {

    private final @NotNull Cache<Location, Player> pumpkin_exploders;
    private final @NotNull Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> no_damage_modifiers;
    private final double expl_effect_radius;

    protected AdjustDamageInfo() {
        this.pumpkin_exploders = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(1)).build();
        this.no_damage_modifiers = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));
        this.expl_effect_radius = PumpkinPVPReloaded.getConfiguration().explosion_effect_radius_squared;
    }

    @Override
    public String configPath() {
        return "pumpkin-deaths.attempt-to-correct-death-details";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath(), true,
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        this.pumpkin_exploders.put(event.getExplodeLocation(), event.getExploder());
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) return;

        final Player damagedPlayer = (Player) event.getEntity();
        final Block explodedBlock = event.getDamager();
        final Player pumpkinExploder = getPumpkinExploder(explodedBlock != null ? explodedBlock.getLocation() : damagedPlayer.getLocation());
        if (pumpkinExploder == null) return;

        EntityDamageByEntityEvent damageByPumpkinExploder;
        try { // Version compatibility
            damageByPumpkinExploder = new EntityDamageByEntityEvent(
                    pumpkinExploder,
                    damagedPlayer,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                    DamageSource.builder(DamageType.PLAYER_EXPLOSION).withDirectEntity(pumpkinExploder).withCausingEntity(pumpkinExploder).build(),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                    no_damage_modifiers,
                    true
            );
        } catch (Throwable t) {
            damageByPumpkinExploder = new EntityDamageByEntityEvent(
                    pumpkinExploder,
                    damagedPlayer,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                    no_damage_modifiers
            );
        }

        if (!damageByPumpkinExploder.callEvent()) {
            event.setCancelled(true);
            return;
        }

        damagedPlayer.setLastDamageCause(damageByPumpkinExploder);
        damagedPlayer.setKiller(pumpkinExploder);
    }

    private @Nullable Player getPumpkinExploder(Location playerLoc) {
        Player closestExploder = null;
        double smallestDistance = expl_effect_radius;

        for (Map.Entry<Location, Player> explosion : this.pumpkin_exploders.asMap().entrySet()) {
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
}