package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.events.PumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
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

public class ExplodeSoundEffects extends PumpkinPVPModule implements Listener {

    private final @NotNull List<Sound> explode_sounds;
    private final float volume, pitch;

    public ExplodeSoundEffects() {
        super("pumpkin-explosion.sound-effects", true,
                "Exploding pumpkins will make a spooky configurable sound.");
        this.volume = config.getFloat(configPath + ".volume", 1.0F);
        this.pitch = config.getFloat(configPath + ".pitch", 1.0F);

        final List<String> defaults = Stream.of(
                 XSound.PARTICLE_SOUL_ESCAPE,
                 XSound.ENTITY_WITCH_CELEBRATE,
                 XSound.ENTITY_GOAT_SCREAMING_DEATH,
                 XSound.ENTITY_ALLAY_DEATH,
                 XSound.ENTITY_CAT_DEATH,
                 XSound.ENTITY_DOLPHIN_HURT,
                 XSound.ENTITY_GOAT_SCREAMING_AMBIENT,
                 XSound.ENTITY_GOAT_SCREAMING_HURT,
                 XSound.ENTITY_HOGLIN_HURT,
                 XSound.ENTITY_CHICKEN_HURT,
                 XSound.ENTITY_ZOMBIFIED_PIGLIN_HURT)
                .filter(XSound::isSupported)
                .map(XSound::parseSound)
                .map(Enum::name)
                .sorted()
                .collect(Collectors.toList());

        this.explode_sounds = config.getList(configPath + ".sounds", defaults,
                "Use multiple entries to randomly cycle through a list of sounds or just one. \n" +
                "Requires correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html")
                .stream()
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
                .collect(Collectors.collectingAndThen(Collectors.toList(), parsedSounds -> {
                    if (parsedSounds.isEmpty()) {
                        parsedSounds.addAll(defaults.stream().map(Sound::valueOf).collect(Collectors.toList()));
                    }
                    return ImmutableList.copyOf(parsedSounds);
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

    @EventHandler(priority = EventPriority.LOW)
    private void onPostPumpkinExplode(PumpkinBlockExplodeEvent event) {
        if (event.hasExploded()) {
            event.getLocation().getWorld().playSound(
                    event.getLocation(),
                    this.explode_sounds.get(Util.RANDOM.nextInt(this.explode_sounds.size())),
                    this.volume,
                    this.pitch
            );
        }
    }
}