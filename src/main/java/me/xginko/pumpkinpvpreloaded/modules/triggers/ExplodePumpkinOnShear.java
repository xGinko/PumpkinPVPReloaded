package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplodePumpkinOnShear extends ExplosionTriggerModule {

    private final boolean shears_take_durability;

    public ExplodePumpkinOnShear() {
        super(TriggerAction.SHEAR, "mechanics.explosion-triggers.shear-pumpkin", false);
        this.shears_take_durability = config.getBoolean(configPath + ".shears-loose-durability", true);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (!config.explosive_pumpkins.contains(event.getClickedBlock().getType())) return;
        if (event.getMaterial() != XMaterial.SHEARS.parseMaterial()) return;

        if (!shears_take_durability) {
            event.setCancelled(true); // Don't cause natural shear behavior
        }

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