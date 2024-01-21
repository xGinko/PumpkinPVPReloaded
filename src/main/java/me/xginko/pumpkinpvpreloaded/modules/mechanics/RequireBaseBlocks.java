package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RequireBaseBlocks implements PumpkinPVPModule, Listener {

    private final HashSet<Material> base_materials;

    public RequireBaseBlocks() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.master().addComment("mechanics.require-specific-base-block.enable",
                "If enabled, pumpkins will only explode when placed on one of the configured materials (like end crystals).");
        this.base_materials = config.getList("mechanics.base.materials", List.of(
                Material.BEDROCK.name(), Material.OBSIDIAN.name(), Material.CRYING_OBSIDIAN.name()),
                "Values need to be valid material enums from bukkit."
        ).stream().map(configuredBase -> {
            try {
                return Material.valueOf(configuredBase);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warning("Base material '"+configuredBase+"' is not a valid Material enum.");
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.require-specific-base-block.enable", false)
                && !base_materials.isEmpty();
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
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        if (!this.base_materials.contains(event.getPumpkin().getRelative(BlockFace.DOWN).getType())) {
            event.setCancelled(true);
        }
    }
}