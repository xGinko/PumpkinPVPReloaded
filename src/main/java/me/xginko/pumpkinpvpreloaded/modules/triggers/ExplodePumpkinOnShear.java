package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.enums.TriggerAction;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class ExplodePumpkinOnShear implements PumpkinPVPModule, Listener {

    private final @Nullable ServerImplementation scheduler;
    private final @NotNull HashSet<Material> pumpkins;
    private final boolean is_folia, shears_take_durability;

    public ExplodePumpkinOnShear() {
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.is_folia = foliaLib.isFolia();
        this.scheduler = is_folia ? foliaLib.getImpl() : null;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.pumpkins = config.explosive_pumpkins;
        this.shears_take_durability = config.getBoolean(configPath() + ".shears-loose-durability", true);
    }

    @Override
    public String configPath() {
        return "mechanics.explosion-triggers.shear-pumpkin";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath() + ".enable", false);
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
        ItemStack interactItem = event.getItem();
        if (interactItem == null || !interactItem.getType().equals(Material.SHEARS)) return;

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

        if (is_folia) {
            scheduler.runAtLocation(explodeLoc, kaboom -> {
                prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);
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
            prePumpkinExplodeEvent.getPumpkin().setType(Material.AIR);
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