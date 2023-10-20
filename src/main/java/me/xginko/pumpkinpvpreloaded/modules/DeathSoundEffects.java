package me.xginko.pumpkinpvpreloaded.modules;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DeathSoundEffects implements PumpkinPVPModule, Listener {

    private final Cache<Location, Float> pumpkinExploders;
    private final List<Sound> deathSounds = new ArrayList<>();

    protected DeathSoundEffects() {
        shouldEnable();
        this.pumpkinExploders = Caffeine.newBuilder().expireAfterWrite(Duration.ofSeconds(1)).build();
        PumpkinPVPReloaded.getConfiguration().getList("pumpkin-deaths.death-sound.sounds", List.of(
                Sound.ENTITY_GOAT_SCREAMING_DEATH.name(),
                Sound.ENTITY_HOGLIN_DEATH.name(),
                Sound.ENTITY_PHANTOM_DEATH.name(),
                Sound.ENTITY_RAVAGER_DEATH.name(),
                Sound.ENTITY_SKELETON_HORSE_DEATH.name(),
                Sound.ENTITY_WARDEN_DEATH.name(),
                Sound.ENTITY_WITCH_CELEBRATE.name(),
                Sound.ENTITY_WITHER_DEATH.name()
        ), """
                Use multiple entries to randomly cycle through a list of sounds or just one.
                Requires correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html
                """
        ).forEach(configuredSound -> {
            try {
                Sound sound = Sound.valueOf(configuredSound);
                this.deathSounds.add(sound);
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warning("Sound '"+configuredSound+"' is not a valid Sound. " +
                        "Please use correct enums from: https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html");
            }
        });
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-deaths.death-sound.enable", false);
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    private void onPrePumpkinExplode(PostPumpkinExplodeEvent event) {
        this.pumpkinExploders.put(event.getExplodeLocation(), event.getExplosionPower());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    private void onPlayerDeath(PlayerDeathEvent event) {
        if (isNearPumpkinExplosion(event.getPlayer().getLocation())) {
            event.setDeathSound(this.deathSounds.get(new Random().nextInt(0, this.deathSounds.size())));
        }
    }

    private boolean isNearPumpkinExplosion(Location playerLoc) {
        for (Map.Entry<Location, Float> explosion : this.pumpkinExploders.asMap().entrySet()) {
            final Location explosionLoc = explosion.getKey();
            if (explosionLoc.getWorld().getUID().equals(playerLoc.getWorld().getUID())) {
                if (playerLoc.distance(explosionLoc) <= explosion.getValue()) {
                    return true;
                }
            }
        }
        return false;
    }
}