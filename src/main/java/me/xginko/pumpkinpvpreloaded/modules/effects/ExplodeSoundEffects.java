package me.xginko.pumpkinpvpreloaded.modules.effects;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ExplodeSoundEffects implements PumpkinPVPModule, Listener {

    private final @NotNull List<Sound> explode_sounds;
    private final float volume, pitch;

    public ExplodeSoundEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.master().addComment(configPath() + ".enable",
                "Exploding pumpkins will make a spooky configurable sound.");
        this.volume = config.getFloat(configPath() + ".volume", 1.0F);
        this.pitch = config.getFloat(configPath() + ".pitch", 1.0F);
        final List<String> defaults = Stream.of(
                "PARTICLE_SOUL_ESCAPE",
                "ENTITY_WITCH_CELEBRATE",
                "ENTITY_GOAT_SCREAMING_DEATH",
                "ENTITY_ALLAY_DEATH",
                "ENTITY_CAT_DEATH",
                "ENTITY_DOLPHIN_HURT",
                "ENTITY_GOAT_SCREAMING_AMBIENT",
                "ENTITY_GOAT_SCREAMING_HURT",
                "ENTITY_HOGLIN_HURT",
                "ENTITY_CHICKEN_HURT",
                "ENTITY_ZOMBIFIED_PIGLIN_HURT")
                .sorted()
                .filter(sound -> {
                    try {
                        Sound.valueOf(sound);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
        this.explode_sounds = config.getList(configPath() + ".sounds", defaults,
                "Use multiple entries to randomly cycle through a list of sounds or just one. \n" +
                "Requires correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html")
                .stream()
                .distinct()
                .map(configuredSound -> {
                    try {
                        return Sound.valueOf(configuredSound);
                    } catch (IllegalArgumentException e) {
                        warn("Sound '" + configuredSound + "' is not a valid Sound. " +
                             "Please use correct enums from: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (this.explode_sounds.isEmpty()) {
            this.explode_sounds.addAll(defaults.stream().map(Sound::valueOf).collect(Collectors.toList()));
        }
    }

    @Override
    public String configPath() {
        return "pumpkin-explosion.sound-effects";
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean(configPath() + ".enable", true)
                && volume > 0;
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

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            event.getExplodeLocation().getWorld().playSound(
                    event.getExplodeLocation(),
                    this.explode_sounds.get(PumpkinPVPReloaded.getRandom().nextInt(this.explode_sounds.size())),
                    this.volume,
                    this.pitch
            );
        }
    }
}