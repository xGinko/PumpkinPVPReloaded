package me.xginko.pumpkinpvpreloaded.modules;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.enums.TriggerAction;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class ExplodePumpkinOnPlace implements PumpkinPVPModule, Listener {

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;

    protected ExplodePumpkinOnPlace() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explosion-triggers.place-pumpkin", false);
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
        Block placed = event.getBlockPlaced();
        if (!MaterialTags.PUMPKINS.isTagged(placed.getType())) return;

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                placed,
                event.getPlayer(),
                placed.getLocation().toCenterLocation(),
                TriggerAction.BLOCK_PLACE
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            if (prePumpkinExplodeEvent.cancelPreceding()) event.setCancelled(true);
            return;
        }

        final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

        regionScheduler.run(plugin, explodeLoc, kaboom -> {
            prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);

            final float power = prePumpkinExplodeEvent.getExplodePower();
            final boolean fire = prePumpkinExplodeEvent.shouldSetFire();
            final boolean breakBlocks = prePumpkinExplodeEvent.shouldBreakBlocks();

            new PostPumpkinExplodeEvent(
                    prePumpkinExplodeEvent.getExploder(),
                    explodeLoc,
                    power,
                    fire,
                    breakBlocks,
                    explodeLoc.getWorld().createExplosion(explodeLoc, power, fire, breakBlocks),
                    TriggerAction.BLOCK_PLACE
            ).callEvent();
        });
    }
}