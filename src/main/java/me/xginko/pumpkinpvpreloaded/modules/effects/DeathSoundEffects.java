package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
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
    private final float volume;

    public DeathSoundEffects() {
        super("pumpkin-deaths.death-sound", true,
                "Players dying to a pumpkin explosion will make a spooky configurable sound.");
        this.volume = config.getFloat(configPath + ".volume", -1.0F,
                "-1 means natural default volume.");
        final List<String> defaults = Stream.of(
                "ENTITY_HOGLIN_DEATH",
                "ENTITY_PHANTOM_DEATH",
                "ENTITY_RAVAGER_DEATH",
                "ENTITY_SKELETON_HORSE_DEATH",
                "ENTITY_WITCH_CELEBRATE",
                "ENTITY_GOAT_SCREAMING_DEATH",
                "ENTITY_WARDEN_DEATH",
                "ENTITY_HORSE_DEATH")
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
                .collect(Collectors.collectingAndThen(Collectors.toList(), ImmutableList::copyOf));
        if (this.death_sounds.isEmpty()) {
            this.death_sounds.addAll(defaults.stream().map(Sound::valueOf).collect(Collectors.toList()));
        }
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (PumpkinPVPReloaded.getTracker().isNearPumpkinExplosion(event.getEntity().getLocation())) {
            event.setDeathSound(this.death_sounds.get(PumpkinPVPReloaded.random().nextInt(this.death_sounds.size())));
            if (volume > 0) event.setDeathSoundVolume(volume);
        }
    }
}