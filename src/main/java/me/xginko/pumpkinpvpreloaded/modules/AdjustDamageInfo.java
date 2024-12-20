package me.xginko.pumpkinpvpreloaded.modules;

import com.cryptomorin.xseries.XEntityType;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import org.bukkit.Location;
import org.bukkit.block.Block;
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

@SuppressWarnings({"deprecation", "removal", "UnstableApiUsage"})
public class AdjustDamageInfo extends PumpkinPVPModule implements Listener {

    private static final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> MODIFIER_FUNCTIONS;
    private static final boolean USE_DAMAGE_SOURCE;

    static {
        MODIFIER_FUNCTIONS = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));
        USE_DAMAGE_SOURCE = Util.hasClass("org.bukkit.damage.DamageSource");
    }

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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        if (event.getEntityType() != XEntityType.PLAYER.get()) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) return;

        final Player damagedPlayer = (Player) event.getEntity();
        final Block explodedBlock = event.getDamager();
        final Location damageLocation = explodedBlock != null ? explodedBlock.getLocation() : damagedPlayer.getLocation();
        final Player pumpkinExploder = PumpkinPVPReloaded.tracker().getPumpkinExploder(damageLocation);
        if (pumpkinExploder == null) return;

        EntityDamageByEntityEvent damageByPumpkinExploder;

        if (USE_DAMAGE_SOURCE) {
            // If available, construct event with DamageSource instead of using the deprecated constructor.
            damageByPumpkinExploder = new EntityDamageByEntityEvent(
                    pumpkinExploder,
                    damagedPlayer,
                    EntityDamageEvent.DamageCause.BLOCK_EXPLOSION,
                    org.bukkit.damage.DamageSource.builder(org.bukkit.damage.DamageType.PLAYER_EXPLOSION)
                            .withCausingEntity(pumpkinExploder)
                            .withDamageLocation(damageLocation).build(),
                    new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, event.getFinalDamage())),
                    MODIFIER_FUNCTIONS,
                    true
            );
        } else {
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
