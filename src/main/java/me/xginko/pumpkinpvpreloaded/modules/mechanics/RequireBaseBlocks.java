package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class RequireBaseBlocks extends PumpkinPVPModule implements Listener {

    private final @NotNull Set<Material> base_materials;

    public RequireBaseBlocks() {
        super("mechanics.require-base-block", false,
                "If enabled, pumpkins will only explode when placed on one of the configured materials (like end crystals).");
        this.base_materials = config.getList(configPath + ".materials", Arrays.asList("BEDROCK", "OBSIDIAN", "CRYING_OBSIDIAN"),
                "Values need to be valid material enums from bukkit.")
                .stream()
                .map(configuredBase -> {
                    try {
                        return Material.valueOf(configuredBase);
                    } catch (IllegalArgumentException e) {
                        warn("Base material '" + configuredBase + "' is not a valid Material enum.");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));
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
    private void onPrePumpkinExplode(PrePumpkinExplodeEvent event) {
        if (!this.base_materials.contains(event.getPumpkin().getRelative(BlockFace.DOWN).getType())) {
            event.setCancelled(true);
        }
    }
}