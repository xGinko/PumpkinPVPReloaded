package me.xginko.pumpkinpvpreloaded.modules.triggers;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
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

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;
    private final HashSet<Material> pumpkins;

    public ExplodePumpkinOnRightClick() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        this.pumpkins = PumpkinPVPReloaded.getConfiguration().explosivePumpkins;
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explosion-triggers.right-click-pumpkin", false);
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
    private void onBlockRightClick(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        final Block clicked = event.getClickedBlock();
        if (clicked == null || !pumpkins.contains(clicked.getType())) return;

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                clicked,
                event.getPlayer(),
                clicked.getLocation().toCenterLocation(),
                TriggerAction.RIGHT_CLICK
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
                    prePumpkinExplodeEvent.getTriggerAction()
            ).callEvent();
        });
    }
}