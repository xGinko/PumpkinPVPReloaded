package me.xginko.pumpkinpvpreloaded.modules.triggers;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinBlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ExplodePumpkinOnPlace extends ExplosionTrigger {

    public ExplodePumpkinOnPlace() {
        super(TriggerAction.PLACE_PUMPKIN, "mechanics.explosion-triggers.place-pumpkin", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!config.explosive_pumpkins.contains(event.getBlock().getType())) return;

        final PrePumpkinBlockExplodeEvent prePumpkinBlockExplodeEvent = new PrePumpkinBlockExplodeEvent(
                triggerAction,
                event.getBlock(),
                event.getPlayer(),
                event.getBlock().getLocation().toCenterLocation()
        );

        if (prePumpkinBlockExplodeEvent.callEvent()) {
            doPumpkinExplosion(prePumpkinBlockExplodeEvent);
        } else {
            event.setCancelled(true);
        }
    }
}