package me.xginko.pumpkinpvpreloaded.modules.triggers;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ExplodePumpkinOnPlace extends ExplosionTriggerModule {

    public ExplodePumpkinOnPlace() {
        super(TriggerAction.BLOCK_PLACE, "mechanics.explosion-triggers.place-pumpkin", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!config.explosive_pumpkins.contains(event.getBlock().getType())) return;

        final PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                event.getBlock(),
                event.getPlayer(),
                event.getBlock().getLocation().toCenterLocation(),
                triggerAction
        );

        if (prePumpkinExplodeEvent.callEvent()) {
            doPumpkinExplosion(prePumpkinExplodeEvent);
        }

        if (prePumpkinExplodeEvent.cancelPreceding()) {
            event.setCancelled(true);
        }
    }
}