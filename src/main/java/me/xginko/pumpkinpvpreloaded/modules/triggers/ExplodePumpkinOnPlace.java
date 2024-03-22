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
import org.bukkit.event.block.BlockPlaceEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ExplodePumpkinOnPlace implements PumpkinPVPModule, Listener {

    private final @Nullable ServerImplementation scheduler;
    private final @NotNull HashSet<Material> pumpkins;
    private final boolean is_folia;

    public ExplodePumpkinOnPlace() {
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.is_folia = foliaLib.isFolia();
        this.scheduler = is_folia ? foliaLib.getImpl() : null;
        this.pumpkins = PumpkinPVPReloaded.getConfiguration().explosive_pumpkins;
    }

    @Override
    public String configPath() {
        return "mechanics.explosion-triggers.place-pumpkin";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath(), false);
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
    private void onBlockPlace(BlockPlaceEvent event) {
        Block placedBlock = event.getBlockPlaced();
        if (!pumpkins.contains(placedBlock.getType())) return;

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

        if (is_folia) {
            scheduler.runAtLocation(explodeLoc, kaboom -> {
                prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);
                new PostPumpkinExplodeEvent(
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
                    TriggerAction.BLOCK_PLACE,
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