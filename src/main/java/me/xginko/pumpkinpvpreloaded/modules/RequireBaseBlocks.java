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

    private final PumpkinPVPReloaded plugin;
    private final HashSet<Material> base_materials = new HashSet<>(3);

    protected RequireBaseBlocks() {
        this.plugin = PumpkinPVPReloaded.getInstance();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.getList("mechanics.base.materials", List.of("CRYING_OBSIDIAN", "OBSIDIAN", "BEDROCK")).forEach(baseMaterial -> {
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
