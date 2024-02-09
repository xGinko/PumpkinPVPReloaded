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

public class ExplodeSoundEffects implements PumpkinPVPModule, Listener {

    private final @NotNull List<Sound> explode_sounds;
    private final float volume, pitch;

    public ExplodeSoundEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        config.master().addComment("pumpkin-explosion.sound-effect.enable",
                "Exploding pumpkins will make a spooky configurable sound.");
        this.volume = config.getFloat("pumpkin-explosion.sound-effect.volume", 1.0F);
        this.pitch = config.getFloat("pumpkin-explosion.sound-effect.volume", 1.0F);
        this.explode_sounds = config.getList("pumpkin-explosion.sound-effect.sounds", List.of(
                        "PARTICLE_SOUL_ESCAPE",
                        "ENTITY_WITCH_CELEBRATE",
                        "ENTITY_GOAT_SCREAMING_DEATH"
                ), """
                Use multiple entries to randomly cycle through a list of sounds or just one.\s
                Requires correct enums from https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html
                """
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
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-explosion.sound-effect.enable", false)
                && !explode_sounds.isEmpty()
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