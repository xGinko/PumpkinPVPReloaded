package me.xginko.pumpkinpvpreloaded.modules.triggers;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplodePumpkinOnLeftClick extends ExplosionTrigger {

    public ExplodePumpkinOnLeftClick() {
        super(TriggerAction.LEFT_CLICK, "mechanics.explosion-triggers.left-click-pumpkin", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;
        if (!config.explosive_pumpkins.contains(event.getClickedBlock().getType())) return;

        final PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                event.getClickedBlock(),
                event.getPlayer(),
                event.getClickedBlock().getLocation().toCenterLocation(),
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