package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XEntityType;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinEntityExplodeEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ExplodePumpkinHeadEntitiesOnDeath extends ExplosionTrigger {

    private final boolean explode_players, only_killed_by_player;

    public ExplodePumpkinHeadEntitiesOnDeath() {
        super(TriggerAction.PUMPKIN_HEAD_ENTITY_DEATH, "mechanics.explosion-triggers.pumpkin-head-entity-kill", false,
                "Entities wearing one of the configured pumpkin blocks on their heads will explode when killed.");
        this.explode_players = config.getBoolean(configPath + ".pumpkin-head-players-also-explode", false);
        this.only_killed_by_player = config.getBoolean(configPath + ".only-when-killed-by-player", true,
                "If disabled will explode on every kind of death.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent event) {
        if (!explode_players && event.getEntityType() == XEntityType.PLAYER.get()) return;

        final LivingEntity dyingEntity = event.getEntity();
        if (only_killed_by_player && dyingEntity.getKiller() == null) return;

        final EntityEquipment equipment = dyingEntity.getEquipment();
        if (equipment == null) return;

        final ItemStack helmet = equipment.getHelmet();
        if (helmet == null || !config.explosive_pumpkins.contains(helmet.getType())) return;

        final PrePumpkinEntityExplodeEvent prePumpkinHeadEntityExplosion = new PrePumpkinEntityExplodeEvent(
                triggerAction,
                dyingEntity,
                dyingEntity.getKiller(),
                dyingEntity.getEyeLocation()
        );

        if (prePumpkinHeadEntityExplosion.callEvent()) {
            doPumpkinExplosion(prePumpkinHeadEntityExplosion);
        }
    }
}