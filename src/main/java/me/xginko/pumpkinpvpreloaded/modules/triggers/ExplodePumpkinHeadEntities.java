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
    private final boolean is_folia, explode_players, only_killed_by_player;

    public ExplodePumpkinHeadEntities() {
        shouldEnable();
        FoliaLib foliaLib = PumpkinPVPReloaded.getFoliaLib();
        this.is_folia = foliaLib.isFolia();
        this.scheduler = is_folia ? foliaLib.getImpl() : null;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.pumpkins = config.explosive_pumpkins;
        config.master().addComment(configPath() + ".enable",
                "Entities wearing one of the configured pumpkin blocks on their heads will explode when killed.");
        this.explode_players = config.getBoolean(configPath() + ".killed-players-also-explode", false);
        this.only_killed_by_player = config.getBoolean(configPath() + ".only-when-killed-by-player", true,
                "If disabled will explode on every kind of death.");
    }

    @Override
    public String configPath() {
        return "mechanics.explosion-triggers.pumpkin-head-entity-kill";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath() + ".enable", true);
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

        if (is_folia) {
            scheduler.runAtLocation(explodeLoc, kaboom -> {
                new PostPumpkinHeadEntityExplodeEvent(
                        preHotHeadEvent.getPumpkinHeadEntity(),
                        preHotHeadEvent.getKiller(),
                        explodeLoc,
                        preHotHeadEvent.getExplodePower(),
                        preHotHeadEvent.shouldSetFire(),
                        preHotHeadEvent.shouldBreakBlocks(),
                        explodeLoc.getWorld().createExplosion(
                                preHotHeadEvent.getPumpkinHeadEntity(),
                                explodeLoc, preHotHeadEvent.getExplodePower(),
                                preHotHeadEvent.shouldSetFire(),
                                preHotHeadEvent.shouldBreakBlocks()
                        )
                ).callEvent();
            });
        } else {
            new PostPumpkinHeadEntityExplodeEvent(
                    preHotHeadEvent.getPumpkinHeadEntity(),
                    preHotHeadEvent.getKiller(),
                    explodeLoc,
                    preHotHeadEvent.getExplodePower(),
                    preHotHeadEvent.shouldSetFire(),
                    preHotHeadEvent.shouldBreakBlocks(),
                    explodeLoc.getWorld().createExplosion(
                            preHotHeadEvent.getPumpkinHeadEntity(),
                            explodeLoc, preHotHeadEvent.getExplodePower(),
                            preHotHeadEvent.shouldSetFire(),
                            preHotHeadEvent.shouldBreakBlocks()
                    )
            ).callEvent();
        }
    }
}