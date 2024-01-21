package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.enums.TriggerAction;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;

public class ExplodePumpkinOnRightClick implements PumpkinPVPModule, Listener {

    private final ServerImplementation scheduler;
    private final HashSet<Material> pumpkins;
    private final boolean isFolia;

    public ExplodePumpkinOnRightClick() {
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        this.pumpkins = PumpkinPVPReloaded.getConfiguration().explosive_pumpkins;
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explosion-triggers.right-click-pumpkin", false);
    }

    @Override
    public void enable() {
        PumpkinPVPReloaded plugin = PumpkinPVPReloaded.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onBlockRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        final Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || !pumpkins.contains(clickedBlock.getType())) return;

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

        if (isFolia) {
            scheduler.runAtLocation(explodeLoc, kaboom -> {
                prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);
                new PostPumpkinExplodeEvent(
                        prePumpkinExplodeEvent.getExploder(),
                        explodeLoc,
                        prePumpkinExplodeEvent.getExplodePower(),
                        prePumpkinExplodeEvent.shouldSetFire(),
                        prePumpkinExplodeEvent.shouldBreakBlocks(),
                        TriggerAction.RIGHT_CLICK
                ).callEvent();
            });
        } else {
            prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);
            new PostPumpkinExplodeEvent(
                    prePumpkinExplodeEvent.getExploder(),
                    explodeLoc,
                    prePumpkinExplodeEvent.getExplodePower(),
                    prePumpkinExplodeEvent.shouldSetFire(),
                    prePumpkinExplodeEvent.shouldBreakBlocks(),
                    TriggerAction.RIGHT_CLICK
            ).callEvent();
        }
    }
}