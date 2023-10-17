package me.xginko.pumpkinpvpreloaded.modules;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.List;

public class RequireBaseBlocks implements PumpkinPVPModule, Listener {

    private final HashSet<Material> base_materials = new HashSet<>(3);

    protected RequireBaseBlocks() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.addComment("mechanics.base.require-for-explosion",
                "If enabled, pumpkins will only explode when placed on one of the configured materials.");
        config.getList("mechanics.base.materials", List.of(
                "CRYING_OBSIDIAN", "OBSIDIAN", "BEDROCK"
        ), "Values need to be valid material enums from bukkit."
        ).forEach(baseMaterial -> {
            try {
                Material material = Material.valueOf(baseMaterial);
                this.base_materials.add(material);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warning("Base material '"+baseMaterial+"' is not a valid Material.");
            }
        });
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("mechanics.base.require-for-explosion", false);
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