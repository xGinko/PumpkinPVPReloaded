package me.xginko.pumpkinpvpreloaded.modules.triggers;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinBlockExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplodePumpkinOnLeftClick extends ExplosionTrigger {

    public ExplodePumpkinOnLeftClick() {
        super(TriggerAction.LEFT_CLICK_PUMPKIN, "mechanics.explosion-triggers.left-click-pumpkin", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (!config.explosive_pumpkins.contains(event.getClickedBlock().getType())) return;

        final PrePumpkinBlockExplodeEvent prePumpkinBlockExplodeEvent = new PrePumpkinBlockExplodeEvent(
                triggerAction,
                event.getClickedBlock(),
                event.getPlayer(),
                event.getClickedBlock().getLocation().toCenterLocation()
        );

        if (prePumpkinBlockExplodeEvent.callEvent()) {
            doPumpkinExplosion(prePumpkinBlockExplodeEvent);
        } else {
            event.setCancelled(true);
        }
    }
}