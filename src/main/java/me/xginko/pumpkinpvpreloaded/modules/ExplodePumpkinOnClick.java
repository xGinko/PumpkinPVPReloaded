package me.xginko.pumpkinpvpreloaded.modules;

import com.destroystokyo.paper.MaterialTags;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
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
import org.bukkit.event.player.PlayerInteractEvent;

public class ExplodePumpkinOnClick implements PumpkinPVPModule, Listener {

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;
    private final boolean triggerOnLeftClick, triggerOnRightClick;

    protected ExplodePumpkinOnClick() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.triggerOnLeftClick = config.getBoolean("mechanics.explosion-triggers.left-click-pumpkin", true);
        this.triggerOnRightClick = config.getBoolean("mechanics.explosion-triggers.right-click-pumpkin", false);
    }

    @Override
    public boolean shouldEnable() {
        return triggerOnLeftClick || triggerOnRightClick;
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
    private void onBlockLeftClick(PlayerInteractEvent event) {
        TriggerAction triggerAction;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK -> {
                if (!triggerOnLeftClick) return;
                triggerAction = TriggerAction.LEFT_CLICK;
            }
            case RIGHT_CLICK_BLOCK -> {
                if (!triggerOnRightClick) return;
                triggerAction = TriggerAction.RIGHT_CLICK;
            }
            default -> { return; }
        }

        final Block clicked = event.getClickedBlock();
        if (clicked == null || !MaterialTags.PUMPKINS.isTagged(clicked.getType())) return;

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                clicked,
                event.getPlayer(),
                clicked.getLocation().toCenterLocation(),
                triggerAction
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