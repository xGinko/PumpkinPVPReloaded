package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.cryptomorin.xseries.XSound;
import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DeathSoundEffects extends PumpkinPVPModule implements Listener {

    private final @NotNull List<Sound> death_sounds;
    private final float volume, pitch;
    private final boolean setVolume, setPitch;

    public DeathSoundEffects() {
        super("pumpkin-deaths.death-sound", true,
                "Players dying to a pumpkin explosion will make a spooky configurable sound.");

        this.setVolume = config.getBoolean(configPath + ".volume.customize", false);
        this.volume = config.getFloat(configPath + ".volume.volume", 1.0F);

        this.setPitch = config.getBoolean(configPath + ".pitch.customize", false);
        this.pitch = config.getFloat(configPath + ".pitch.pitch", 1.0F);

        final List<String> defaults = Stream.of(
                 XSound.ENTITY_HOGLIN_DEATH,
                 XSound.ENTITY_PHANTOM_DEATH,
                 XSound.ENTITY_RAVAGER_DEATH,
                 XSound.ENTITY_SKELETON_HORSE_DEATH,
                 XSound.ENTITY_WITCH_CELEBRATE,
                 XSound.ENTITY_GOAT_SCREAMING_DEATH,
                 XSound.ENTITY_WARDEN_DEATH,
                 XSound.ENTITY_HORSE_DEATH)
                .filter(XSound::isSupported)
                .map(XSound::parseSound)
                .map(Enum::name)
                .sorted()
                .collect(Collectors.toList());

        this.death_sounds = config.getList(configPath + ".sounds", defaults,
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

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (!PumpkinPVPReloaded.getTracker().isNearPumpkinExplosion(event.getEntity().getLocation())) return;

        event.setDeathSound(this.death_sounds.get(Util.RANDOM.nextInt(this.death_sounds.size())));

        if (setVolume) {
            event.setDeathSoundVolume(volume);
        }

        if (setPitch) {
            event.setDeathSoundPitch(pitch);
        }
    }
}