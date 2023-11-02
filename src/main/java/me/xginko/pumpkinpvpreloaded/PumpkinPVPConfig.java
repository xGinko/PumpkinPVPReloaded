package me.xginko.pumpkinpvpreloaded;

import io.github.thatsmusic99.configurationmaster.api.ConfigFile;
import io.github.thatsmusic99.configurationmaster.api.ConfigSection;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class PumpkinPVPConfig {

    private final @NotNull ConfigFile config;
    public final HashSet<Material> explosivePumpkins = new HashSet<>(2);
    public final float explosion_power;
    public final boolean explosion_set_fire, explosion_break_blocks;

    protected PumpkinPVPConfig() throws Exception {
        this.config = loadConfig(new File(PumpkinPVPReloaded.getInstance().getDataFolder(), "config.yml"));
        getList("pumpkin-explosion.pumpkin-types", List.of(
                Material.PUMPKIN.name(), Material.CARVED_PUMPKIN.name()
        )).forEach(configuredMaterial -> {
            try {
                Material material = Material.valueOf(configuredMaterial);
                this.explosivePumpkins.add(material);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warning("Material '"+configuredMaterial+"' cant be used as " +
                        "an explosive pumpkin because its not a valid material.");
            }
        });
        this.explosion_power = getFloat("pumpkin-explosion.power", 8.0F,
                "TNT has a power of 4.0");
        this.explosion_set_fire = getBoolean("pumpkin-explosion.set-fire", true,
                "Enable explosion fire like with respawn anchors.");
        this.explosion_break_blocks = getBoolean("pumpkin-explosion.break-blocks", true,
                "Enable destruction of nearby blocks.");
    }

    private ConfigFile loadConfig(File ymlFile) throws Exception {
        File parent = new File(ymlFile.getParent());
        if (!parent.exists() && !parent.mkdir())
            PumpkinPVPReloaded.getLog().severe("Unable to create plugin config directory.");
        if (!ymlFile.exists())
            ymlFile.createNewFile();
        return ConfigFile.loadConfig(ymlFile);
    }

    public void saveConfig() {
        try {
            config.save();
        } catch (Exception e) {
            PumpkinPVPReloaded.getLog().severe("Failed to save config file! - " + e.getLocalizedMessage());
        }
    }

    public @NotNull ConfigFile master() {
        return config;
    }

    public boolean getBoolean(@NotNull String path, boolean def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getBoolean(path, def);
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getString(path, def);
    }

    public @NotNull String getString(@NotNull String path, @NotNull String def) {
        config.addDefault(path, def);
        return config.getString(path, def);
    }

    public double getDouble(@NotNull String path, double def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getDouble(path, def);
    }

    public double getDouble(@NotNull String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, def);
    }

    public int getInt(@NotNull String path, int def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getInteger(path, def);
    }

    public int getInt(@NotNull String path, int def) {
        config.addDefault(path, def);
        return config.getInteger(path, def);
    }

    public float getFloat(@NotNull String path, float def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getFloat(path, def);
    }

    public float getFloat(@NotNull String path, float def) {
        config.addDefault(path, def);
        return config.getFloat(path, def);
    }

    public @NotNull List<String> getList(@NotNull String path, @NotNull List<String> def, @NotNull String comment) {
        config.addDefault(path, def, comment);
        return config.getStringList(path);
    }

    public @NotNull List<String> getList(@NotNull String path, @NotNull List<String> def) {
        config.addDefault(path, def);
        return config.getStringList(path);
    }

    public @NotNull ConfigSection getConfigSection(@NotNull String path, @NotNull Map<String, Object> defaultKeyValue) {
        config.addDefault(path, null);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path+"."+string, object));
        return config.getConfigSection(path);
    }

    public @NotNull ConfigSection getConfigSection(@NotNull String path, @NotNull Map<String, Object> defaultKeyValue, @NotNull String comment) {
        config.addDefault(path, null, comment);
        config.makeSectionLenient(path);
        defaultKeyValue.forEach((string, object) -> config.addExample(path+"."+string, object));
        return config.getConfigSection(path);
    }

    public void addComment(@NotNull String path, @NotNull String comment) {
        config.addComment(path, comment);
    }
}
