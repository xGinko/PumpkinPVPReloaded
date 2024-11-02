package me.xginko.pumpkinpvpreloaded.modules;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
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

import java.util.EnumMap;
import java.util.Map;

public class AdjustDamageInfo extends PumpkinPVPModule implements Listener {

    private static final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>>
            MODIFIER_FUNCTIONS = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));

    public AdjustDamageInfo() {
        super("pumpkin-deaths.attempt-to-correct-death-details", true,
                "Tries to fill in the blanks so the game can roughly tell who killed who.");
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @SuppressWarnings({"deprecation", "removal", "UnstableApiUsage"})
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) return;

        final Player damagedPlayer = (Player) event.getEntity();
        final Block explodedBlock = event.getDamager();
        final Location damageLocation = explodedBlock != null ? explodedBlock.getLocation() : damagedPlayer.getLocation();
        final Player pumpkinExploder = PumpkinPVPReloaded.getTracker().getPumpkinExploder(damageLocation);
        if (pumpkinExploder == null) return;

        EntityDamageByEntityEvent damageByPumpkinExploder;
        try { // Version compatibility
            damageByPumpkinExploder = new EntityDamageByEntityEvent(
                    pumpkinExploder,
                    damagedPlayer,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                    DamageSource.builder(DamageType.PLAYER_EXPLOSION).withCausingEntity(pumpkinExploder).withDamageLocation(damageLocation).build(),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                    MODIFIER_FUNCTIONS,
                    true
            );
        } catch (Throwable t) {
            damageByPumpkinExploder = new EntityDamageByEntityEvent(
                    pumpkinExploder,
                    damagedPlayer,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                    MODIFIER_FUNCTIONS
            );
        }

        if (damageByPumpkinExploder.callEvent()) {
            damagedPlayer.setLastDamageCause(damageByPumpkinExploder);
            damagedPlayer.setKiller(pumpkinExploder);
        } else {
            event.setCancelled(true);
        }
    }
}
