package me.xginko.pumpkinpvpreloaded.modules.triggers;

import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;

public class ExplodePumpkinHeadEntities implements PumpkinPVPModule, Listener {

    private final PumpkinPVPReloaded plugin;
    private final RegionScheduler regionScheduler;
    private final HashSet<Material> pumpkins;
    private final boolean explode_players, only_killed_by_player;

    public ExplodePumpkinHeadEntities() {
        shouldEnable();
        this.plugin = PumpkinPVPReloaded.getInstance();
        this.regionScheduler = plugin.getServer().getRegionScheduler();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.pumpkins = config.explosivePumpkins;
        config.addComment("mechanics.explosion-triggers.pumpkin-head-entity-kill.enable",
                "Entities wearing one of the configured pumpkin blocks on their heads will explode as a funny addition.");
        this.explode_players = config.getBoolean("mechanics.explosion-triggers.pumpkin-head-entity-kill.killed-players-also-explode", false);
        this.only_killed_by_player = config.getBoolean("mechanics.explosion-triggers.pumpkin-head-entity-kill.only-when-killed-by-player", true,
                "If disabled will explode on every kind of death.");
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.explosion-triggers.pumpkin-head-entity-kill.enable", true);
    }

    @Override
    public void enable() {
        PumpkinPVPReloaded plugin = PumpkinPVPReloaded.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll();
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    private void onEntityDeath(EntityDeathEvent event) {
        final LivingEntity dying = event.getEntity();
        if (dying.getType().equals(EntityType.PLAYER) && !explode_players) return;
        if (only_killed_by_player && dying.getKiller() == null) return;

        final EntityEquipment equipment = dying.getEquipment();
        if (equipment == null) return;
        final ItemStack helmet = equipment.getHelmet();
        if (helmet == null || !pumpkins.contains(helmet.getType())) return;

        PrePumpkinHeadEntityExplodeEvent preHotHeadEvent = new PrePumpkinHeadEntityExplodeEvent(
                dying,
                dying.getKiller(),
                dying.getEyeLocation()
        );

        if (!preHotHeadEvent.callEvent()) return;
        final Location explodeLoc = preHotHeadEvent.getExplodeLocation();

        regionScheduler.run(plugin, explodeLoc, kaboom -> {
            new PostPumpkinHeadEntityExplodeEvent(
                    preHotHeadEvent.getPumpkinHeadEntity(),
                    preHotHeadEvent.getKiller(),
                    explodeLoc,
                    preHotHeadEvent.getExplodePower(),
                    preHotHeadEvent.shouldSetFire(),
                    preHotHeadEvent.shouldBreakBlocks()
            ).callEvent();
        });
    }
}