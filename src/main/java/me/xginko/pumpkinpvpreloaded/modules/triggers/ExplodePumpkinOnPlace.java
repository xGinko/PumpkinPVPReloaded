package me.xginko.pumpkinpvpreloaded.modules.triggers;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.utils.TriggerAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;

public class ExplodePumpkinOnPlace extends ExplosionTrigger {

    public ExplodePumpkinOnPlace() {
        super("mechanics.explosion-triggers.place-pumpkin", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockPlace(BlockPlaceEvent event) {
        if (!config.explosive_pumpkins.contains(event.getBlock().getType())) return;

        final PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                event.getBlock(),
                event.getPlayer(),
                event.getBlock().getLocation().toCenterLocation(),
                TriggerAction.BLOCK_PLACE
        );

        if (prePumpkinExplodeEvent.callEvent()) {
            doPumpkinExplosion(TriggerAction.BLOCK_PLACE, prePumpkinExplodeEvent);
        }

        if (prePumpkinExplodeEvent.cancelPreceding()) {
            event.setCancelled(true);
        }
    }
}