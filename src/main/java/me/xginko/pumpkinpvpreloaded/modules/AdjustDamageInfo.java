package me.xginko.pumpkinpvpreloaded.modules;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.EnumMap;
import java.util.Map;

public class AdjustDamageInfo implements PumpkinPVPModule, Listener {

    private final Map<EntityDamageEvent.DamageModifier, Double> damageModifier;
    private final Map<EntityDamageEvent.DamageModifier, ? extends Function<? super Double, Double>> modifierFunctions;

    /*
    *  This is how it looks for the server on explosion.
    *
    *  [23:08:04 INFO]: [PumpkinPVP] EntityDamageByBlockEvent
    *  [23:08:04 INFO]: [PumpkinPVP] Player: xGinko
    *  [23:08:04 INFO]: [PumpkinPVP] DamageCause: BLOCK_EXPLOSION
    *  [23:08:04 INFO]: [PumpkinPVP] Damage Source: null
    *
    * */

    protected AdjustDamageInfo() {
        this.damageModifier = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Double.MAX_VALUE));
        this.modifierFunctions = new EnumMap<>(ImmutableMap.of(EntityDamageEvent.DamageModifier.BASE, Functions.constant(-0.0)));
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("attempt-to-correct-death-details", true);
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamage(EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;




        PumpkinPVPReloaded.getLog().info(event.getEventName());
        PumpkinPVPReloaded.getLog().info("Player: "+event.getEntity().getName());
        PumpkinPVPReloaded.getLog().info("DamageCause: "+event.getCause().name());
    }


    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onDamageByBlock(EntityDamageByBlockEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;

        PumpkinPVPReloaded.getLog().info(event.getEventName());
        PumpkinPVPReloaded.getLog().info("Player: "+event.getEntity().getName());
        PumpkinPVPReloaded.getLog().info("DamageCause: "+event.getCause().name());
        PumpkinPVPReloaded.getLog().info("Damage Source: "+event.getDamager());
    }
}