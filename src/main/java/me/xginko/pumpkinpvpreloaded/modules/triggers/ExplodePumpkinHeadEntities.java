package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XEntityType;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinHeadEntityExplodeEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ExplodePumpkinHeadEntities extends ExplosionTrigger {

    private final boolean explode_players, only_killed_by_player;

    public ExplodePumpkinHeadEntities() {
        super("mechanics.explosion-triggers.pumpkin-head-entity-kill", false,
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

        final PrePumpkinHeadEntityExplodeEvent prePumpkinHeadEvent = new PrePumpkinHeadEntityExplodeEvent(
                dyingEntity,
                dyingEntity.getKiller(),
                dyingEntity.getEyeLocation()
        );

        if (!prePumpkinHeadEvent.callEvent()) return;

        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(prePumpkinHeadEvent.getExplodeLocation())
                    .run(() -> explodeAndCallEvent(prePumpkinHeadEvent));
        } else {
            explodeAndCallEvent(prePumpkinHeadEvent);
        }
    }

    private void explodeAndCallEvent(PrePumpkinHeadEntityExplodeEvent prePumpkinHeadEvent) {
        plugin.getServer().getPluginManager().callEvent(new PostPumpkinHeadEntityExplodeEvent(
                prePumpkinHeadEvent.getPumpkinHeadEntity(),
                prePumpkinHeadEvent.getKiller(),
                prePumpkinHeadEvent.getExplodeLocation(),
                prePumpkinHeadEvent.getExplodePower(),
                prePumpkinHeadEvent.shouldSetFire(),
                prePumpkinHeadEvent.shouldBreakBlocks(),
                prePumpkinHeadEvent.getExplodeLocation().getWorld().createExplosion(
                        prePumpkinHeadEvent.getPumpkinHeadEntity(),
                        prePumpkinHeadEvent.getExplodeLocation(),
                        prePumpkinHeadEvent.getExplodePower(),
                        prePumpkinHeadEvent.shouldSetFire(),
                        prePumpkinHeadEvent.shouldBreakBlocks()
                )
        ));
    }
}