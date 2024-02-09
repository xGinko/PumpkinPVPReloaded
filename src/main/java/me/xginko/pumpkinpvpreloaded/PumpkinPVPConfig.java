package me.xginko.pumpkinpvpreloaded;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class PumpkinPVPConfig {

    private final @NotNull ConfigFile configFile;
    public final @NotNull HashSet<Material> explosive_pumpkins;
    public final double explosion_effect_radius_squared;
    public final float explosion_power;
    public final boolean explosion_set_fire, explosion_break_blocks;

    protected PumpkinPVPConfig() throws Exception {
        // Create plugin folder first if it does not exist yet
        File pluginFolder = PumpkinPVPReloaded.getInstance().getDataFolder();
        if (!pluginFolder.exists() && !pluginFolder.mkdir())
            PumpkinPVPReloaded.getLog().error("Failed to create plugin folder.");
        // Load config.yml with ConfigMaster
        this.configFile = ConfigFile.loadConfig(new File(pluginFolder, "config.yml"));
        this.explosive_pumpkins = this.getList("pumpkin-explosion.pumpkin-types", List.of(
                        Material.PUMPKIN.name(), Material.CARVED_PUMPKIN.name(), Material.JACK_O_LANTERN.name())
        ).stream().map(configuredMaterial -> {
            try {
                return Material.valueOf(configuredMaterial);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warn("Material '"+configuredMaterial+"' cant be used as " +
                        "an explosive pumpkin because its not a valid material.");
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toCollection(HashSet::new));
        this.explosion_power = getFloat("pumpkin-explosion.power", 8.0F,
                "TNT has a power of 4.0");
        this.explosion_effect_radius_squared = Math.pow(Math.max(explosion_power, 3), 2);
        this.explosion_set_fire = getBoolean("pumpkin-explosion.set-fire", true,
                "Enable explosion fire like with respawn anchors.");
        this.explosion_break_blocks = getBoolean("pumpkin-explosion.break-blocks", true,
                "Enable destruction of nearby blocks.");
    }

    public void saveConfig() {
        try {
            this.configFile.save();
        } catch (Exception e) {
            PumpkinPVPReloaded.getLog().error("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public @NotNull ConfigFile master() {
        return configFile;
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getBoolean(path, def);
    }

    public boolean getBoolean(@NotNull String path, boolean def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getBoolean(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getInteger(path, def);
    }

    public int getInt(@NotNull String path, int def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getInteger(path, def);
    }

    public float getFloat(@NotNull String path, float def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getFloat(path, def);
    }

    public float getFloat(@NotNull String path, float def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getFloat(path, def);
    }

    public double getDouble(@NotNull String path, double def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getDouble(path, def);
    }

    public double getDouble(@NotNull String path, double def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getDouble(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getString(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getString(path, def);
    }

    public @NotNull List<String> getList(@NotNull String path, @NotNull List<String> def) {
        this.configFile.addDefault(path, def);
        return this.configFile.getStringList(path);
    }

    public @NotNull List<String> getList(@NotNull String path, @NotNull List<String> def, @NotNull String comment) {
        this.configFile.addDefault(path, def, comment);
        return this.configFile.getStringList(path);
    }
}