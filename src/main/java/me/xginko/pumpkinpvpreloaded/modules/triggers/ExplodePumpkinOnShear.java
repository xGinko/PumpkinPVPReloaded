package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.TriggerAction;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class ExplodePumpkinOnShear extends PumpkinPVPModule implements Listener {

    private final boolean shears_take_durability;

    public ExplodePumpkinOnShear() {
        super("mechanics.explosion-triggers.shear-pumpkin", false);
        this.shears_take_durability = config.getBoolean(configPath + ".shears-loose-durability", true);
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
        ItemStack interactItem = event.getItem();
        if (interactItem == null || !interactItem.getType().equals(XMaterial.SHEARS.parseMaterial())) return;

        if (!shears_take_durability) event.setCancelled(true); // Don't cause natural shear behavior
        final Player originalExploder = event.getPlayer();

        PrePumpkinExplodeEvent prePumpkinExplodeEvent = new PrePumpkinExplodeEvent(
                clickedBlock,
                originalExploder,
                clickedBlock.getLocation().toCenterLocation(),
                TriggerAction.SHEAR
        );

        if (!prePumpkinExplodeEvent.callEvent()) {
            event.setCancelled(prePumpkinExplodeEvent.cancelPreceding());
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
                        TriggerAction.SHEAR,
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
                    TriggerAction.SHEAR,
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