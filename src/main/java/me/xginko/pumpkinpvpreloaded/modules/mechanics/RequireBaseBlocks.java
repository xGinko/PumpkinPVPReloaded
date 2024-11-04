package me.xginko.pumpkinpvpreloaded.modules.mechanics;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RequireBaseBlocks extends PumpkinPVPModule implements Listener {

    private final @NotNull Set<Material> base_materials;

    public RequireBaseBlocks() {
        super("mechanics.require-base-block", false,
                "If enabled, pumpkins will only explode when placed on one of the configured materials.");

        List<String> defaults = Stream.of(
                XMaterial.BEDROCK,
                XMaterial.OBSIDIAN,
                XMaterial.CRYING_OBSIDIAN)
                .filter(XMaterial::isSupported)
                .map(XMaterial::parseMaterial)
                .map(Enum::name)
                .sorted()
                .collect(Collectors.toList());

        this.base_materials = config.getList(configPath + ".materials", defaults,
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
                .collect(Collectors.collectingAndThen(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)), parsedMaterials -> {
                    if (parsedMaterials.isEmpty()) {
                        parsedMaterials.addAll(defaults.stream().map(Material::valueOf).collect(Collectors.toList()));
                    }
                    return parsedMaterials;
                }));
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