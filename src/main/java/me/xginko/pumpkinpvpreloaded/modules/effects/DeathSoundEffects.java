package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class DeathSoundEffects implements PumpkinPVPModule, Listener {

    private final @NotNull Cache<Location, Boolean> pumpkin_explosion;
    private final @NotNull List<Sound> death_sounds;
    private final double expl_effect_radius;
    private final float volume;

    public DeathSoundEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.expl_effect_radius = config.explosion_effect_radius_squared;
        config.master().addComment("pumpkin-deaths.death-sound.enable",
                "Players dying to a pumpkin explosion will make a spooky configurable sound.");
        this.volume = config.getFloat("pumpkin-deaths.death-sound.volume", -1.0F,
                "-1 means natural default volume.");
        this.pumpkin_explosion = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(1)).build();
        final List<String> defaults = Stream.of(
                "ENTITY_HOGLIN_DEATH",
                "ENTITY_PHANTOM_DEATH",
                "ENTITY_RAVAGER_DEATH",
                "ENTITY_SKELETON_HORSE_DEATH",
                "ENTITY_WITCH_CELEBRATE",
                "ENTITY_GOAT_SCREAMING_DEATH",
                "ENTITY_WARDEN_DEATH"
                ).filter(sound -> {
                    try {
                        Sound.valueOf(sound);
                        return true;
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }).sorted().toList();
        this.death_sounds = config.getList("pumpkin-deaths.death-sound.sounds", defaults, """
                Use multiple entries to randomly cycle through a list of sounds or just one.\s
                Requires correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html"""
        ).stream().map(configuredSound -> {
            try {
                return Sound.valueOf(configuredSound);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warn("Sound '"+configuredSound+"' is not a valid Sound. " +
                        "Please use correct enums from: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html");
                return null;
            }
        }).filter(Objects::nonNull).distinct().toList();
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-deaths.death-sound.enable", false)
                && !death_sounds.isEmpty();
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            this.pumpkin_explosion.put(event.getExplodeLocation(), true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded()) {
            this.pumpkin_explosion.put(event.getExplodeLocation(), true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (this.isNearPumpkinExplosion(event.getEntity().getLocation())) {
            event.setDeathSound(this.death_sounds.get(PumpkinPVPReloaded.getRandom().nextInt(this.death_sounds.size())));
            if (volume > 0) event.setDeathSoundVolume(volume);
        }
    }

    private boolean isNearPumpkinExplosion(Location playerLoc) {
        for (Map.Entry<Location, Boolean> explosion : this.pumpkin_explosion.asMap().entrySet()) {
            if (explosion.getKey().getWorld().getUID().equals(playerLoc.getWorld().getUID())) {
                if (playerLoc.distanceSquared(explosion.getKey()) <= expl_effect_radius) {
                    return true;
                }
            }
        }
        return false;
    }
}