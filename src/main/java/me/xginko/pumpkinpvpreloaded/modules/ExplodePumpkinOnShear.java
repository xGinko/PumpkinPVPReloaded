package me.xginko.pumpkinpvpreloaded.modules;

import io.papermc.paper.event.block.PlayerShearBlockEvent;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.enums.TriggerAction;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.HashSet;

public class ExplodePumpkinOnShear implements PumpkinPVPModule, Listener {

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;
    private final HashSet<Material> pumpkins;
    private final boolean shears_take_durability;
    private final int dura_reduction;

    protected ExplodePumpkinOnShear() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.pumpkins = config.explosivePumpkins;
        this.shears_take_durability = config.getBoolean("mechanics.explosion-triggers.shear-pumpkin.shears-take-durability", true);
        this.dura_reduction = config.getInt("mechanics.explosion-triggers.shear-pumpkin.dura-per-explosion", 1);
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explosion-triggers.shear-pumpkin.enable", false);
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
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;

        final Block clicked = event.getClickedBlock();
        if (clicked == null || !pumpkins.contains(clicked.getType())) return;
        ItemStack interactItem = event.getItem();
        if (interactItem == null || !interactItem.getType().equals(Material.SHEARS)) return;

        final Player originalExploder = event.getPlayer();

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                clicked,
                originalExploder,
                clicked.getLocation().toCenterLocation(),
                TriggerAction.SHEAR
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            event.setCancelled(prePumpkinExplodeEvent.cancelPreceding());
            return;
        }

        final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

        regionScheduler.run(plugin, explodeLoc, kaboom -> {
            prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);

            final float power = prePumpkinExplodeEvent.getExplodePower();
            final boolean fire = prePumpkinExplodeEvent.shouldSetFire();
            final boolean breakBlocks = prePumpkinExplodeEvent.shouldBreakBlocks();

            PostPumpkinExplodeEvent postPumpkinExplodeEvent = new PostPumpkinExplodeEvent(
                    prePumpkinExplodeEvent.getExploder(),
                    explodeLoc,
                    power,
                    fire,
                    breakBlocks,
                    explodeLoc.getWorld().createExplosion(explodeLoc, power, fire, breakBlocks),
                    TriggerAction.SHEAR
            );

            postPumpkinExplodeEvent.callEvent();

            if (
                    shears_take_durability
                    && postPumpkinExplodeEvent.hasExploded()
                    && originalExploder.getUniqueId().equals(postPumpkinExplodeEvent.getExploder().getUniqueId())
            ) {
                interactItem.editMeta(Damageable.class, meta -> {
                    meta.setDamage(meta.hasDamage() ? meta.getDamage() + dura_reduction : dura_reduction);
                });
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onShearBlock(PlayerShearBlockEvent event) {
        final Block sheared = event.getBlock();
        if (!pumpkins.contains(sheared.getType())) return;
        ItemStack interactItem = event.getItem();
        if (!interactItem.getType().equals(Material.SHEARS)) return;

        final Player originalExploder = event.getPlayer();

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                sheared,
                originalExploder,
                sheared.getLocation().toCenterLocation(),
                TriggerAction.SHEAR
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            event.setCancelled(prePumpkinExplodeEvent.cancelPreceding());
            return;
        }

        final Location explodeLoc = prePumpkinExplodeEvent.getExplodeLocation();

        regionScheduler.run(plugin, explodeLoc, kaboom -> {
            prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);

            final float power = prePumpkinExplodeEvent.getExplodePower();
            final boolean fire = prePumpkinExplodeEvent.shouldSetFire();
            final boolean breakBlocks = prePumpkinExplodeEvent.shouldBreakBlocks();

            PostPumpkinExplodeEvent postPumpkinExplodeEvent = new PostPumpkinExplodeEvent(
                    prePumpkinExplodeEvent.getExploder(),
                    explodeLoc,
                    power,
                    fire,
                    breakBlocks,
                    explodeLoc.getWorld().createExplosion(explodeLoc, power, fire, breakBlocks),
                    TriggerAction.SHEAR
            );

            postPumpkinExplodeEvent.callEvent();

            if (
                    !shears_take_durability
                    && postPumpkinExplodeEvent.hasExploded()
                    && originalExploder.getUniqueId().equals(postPumpkinExplodeEvent.getExploder().getUniqueId())
            ) {
                event.setCancelled(true);
            }
        });
    }
}