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
import org.bukkit.event.block.BlockPlaceEvent;

public class ExplodePumpkinOnPlace extends PumpkinPVPModule implements Listener {

    public ExplodePumpkinOnPlace() {
        super("mechanics.explosion-triggers.place-pumpkin", false);
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
    private void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if (!config.explosive_pumpkins.contains(placedBlock.getType())) return;

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                placedBlock,
                event.getPlayer(),
                placedBlock.getLocation().toCenterLocation(),
                TriggerAction.BLOCK_PLACE
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            if (prePumpkinExplodeEvent.cancelPreceding()) event.setCancelled(true);
            return;
        }

        final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

        // Remove pumpkin before creating explosion
        prePumpkinExplodeEvent.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);

        PostPumpkinExplodeEvent postPumpkinExplodeEvent = new PostPumpkinExplodeEvent(
                prePumpkinExplodeEvent.getExploder(),
                explodeLoc,
                prePumpkinExplodeEvent.getExplodePower(),
                prePumpkinExplodeEvent.shouldSetFire(),
                prePumpkinExplodeEvent.shouldBreakBlocks(),
                TriggerAction.BLOCK_PLACE,
                explodeLoc.getWorld().createExplosion(
                        explodeLoc,
                        prePumpkinExplodeEvent.getExplodePower(),
                        prePumpkinExplodeEvent.shouldSetFire(),
                        prePumpkinExplodeEvent.shouldBreakBlocks()
                )
        );

        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(explodeLoc).run(postPumpkinExplodeEvent::callEvent);
        } else {
            postPumpkinExplodeEvent.callEvent();
        }
    }
}