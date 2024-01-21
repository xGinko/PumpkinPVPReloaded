package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.tcoded.folialib.FoliaLib;
import com.tcoded.folialib.impl.ServerImplementation;
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

    private final ServerImplementation scheduler;
    private final HashSet<Material> pumpkins;
    private final boolean isFolia, explode_players, only_killed_by_player;

    public ExplodePumpkinHeadEntities() {
        shouldEnable();
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.isFolia = foliaLib.isFolia();
        this.scheduler = isFolia ? foliaLib.getImpl() : null;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.pumpkins = config.explosive_pumpkins;
        config.master().addComment("mechanics.explosion-triggers.pumpkin-head-entity-kill.enable",
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
        final LivingEntity dyingEntity = event.getEntity();
        if (dyingEntity.getType().equals(EntityType.PLAYER) && !explode_players) return;
        if (only_killed_by_player && dyingEntity.getKiller() == null) return;

        final EntityEquipment equipment = dyingEntity.getEquipment();
        if (equipment == null) return;
        final ItemStack helmet = equipment.getHelmet();
        if (helmet == null || !pumpkins.contains(helmet.getType())) return;

        PrePumpkinHeadEntityExplodeEvent preHotHeadEvent = new PrePumpkinHeadEntityExplodeEvent(
                dyingEntity,
                dyingEntity.getKiller(),
                dyingEntity.getEyeLocation()
        );

        if (!preHotHeadEvent.callEvent()) return;
        final Location explodeLoc = preHotHeadEvent.getExplodeLocation();

        if (isFolia) {
            scheduler.runAtLocation(explodeLoc, kaboom -> {
                new PostPumpkinHeadEntityExplodeEvent(
                        preHotHeadEvent.getPumpkinHeadEntity(),
                        preHotHeadEvent.getKiller(),
                        explodeLoc,
                        preHotHeadEvent.getExplodePower(),
                        preHotHeadEvent.shouldSetFire(),
                        preHotHeadEvent.shouldBreakBlocks()
                ).callEvent();
            });
        } else {
            new PostPumpkinHeadEntityExplodeEvent(
                    preHotHeadEvent.getPumpkinHeadEntity(),
                    preHotHeadEvent.getKiller(),
                    explodeLoc,
                    preHotHeadEvent.getExplodePower(),
                    preHotHeadEvent.shouldSetFire(),
                    preHotHeadEvent.shouldBreakBlocks()
            ).callEvent();
        }
    }
}