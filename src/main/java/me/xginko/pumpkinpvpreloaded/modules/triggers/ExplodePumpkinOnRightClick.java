package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.TriggerAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplodePumpkinOnRightClick extends PumpkinPVPModule implements Listener {

    public ExplodePumpkinOnRightClick() {
        super("mechanics.explosion-triggers.right-click-pumpkin", false);
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
    private void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !config.explosive_pumpkins.contains(clickedBlock.getType())) return;

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                clickedBlock,
                event.getPlayer(),
                clickedBlock.getLocation().toCenterLocation(),
                TriggerAction.RIGHT_CLICK
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            if (prePumpkinExplodeEvent.cancelPreceding()) event.setCancelled(true);
            return;
        }

        final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(explodeLoc).run(() -> {
                prePumpkinExplodeEvent.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);

                new PostPumpkinExplodeEvent(
                        prePumpkinExplodeEvent.getExploder(),
                        explodeLoc,
                        prePumpkinExplodeEvent.getExplodePower(),
                        prePumpkinExplodeEvent.shouldSetFire(),
                        prePumpkinExplodeEvent.shouldBreakBlocks(),
                        TriggerAction.RIGHT_CLICK,
                        explodeLoc.getWorld().createExplosion(
                                explodeLoc,
                                prePumpkinExplodeEvent.getExplodePower(),
                                prePumpkinExplodeEvent.shouldSetFire(),
                                prePumpkinExplodeEvent.shouldBreakBlocks()
                        )
                ).callEvent();
            });
        } else {
            prePumpkinExplodeEvent.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);

            new PostPumpkinExplodeEvent(
                    prePumpkinExplodeEvent.getExploder(),
                    explodeLoc,
                    prePumpkinExplodeEvent.getExplodePower(),
                    prePumpkinExplodeEvent.shouldSetFire(),
                    prePumpkinExplodeEvent.shouldBreakBlocks(),
                    TriggerAction.RIGHT_CLICK,
                    explodeLoc.getWorld().createExplosion(
                            explodeLoc,
                            prePumpkinExplodeEvent.getExplodePower(),
                            prePumpkinExplodeEvent.shouldSetFire(),
                            prePumpkinExplodeEvent.shouldBreakBlocks()
                    )
            ).callEvent();
        }
    }
}