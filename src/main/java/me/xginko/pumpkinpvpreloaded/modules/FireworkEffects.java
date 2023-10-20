package me.xginko.pumpkinpvpreloaded.modules;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class FireworkEffects implements PumpkinPVPModule, Listener {

    private final List<FireworkEffect> fireWorkEffects = new ArrayList<>();

    protected FireworkEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        final boolean flicker = config.getBoolean("pumpkin-explosion.firework-effects.flicker", false);
        final boolean trail = config.getBoolean("pumpkin-explosion.firework-effects.trail", false);
        final List<Color> halloweenColors = List.of(
                Color.fromRGB(255, 174, 3),     // Pumpkin Light Orange
                Color.fromRGB(254, 78, 0),      // Pumpkin Dark Orange
                Color.fromRGB(26, 9, 13),       // Witch Hat Dark Purple
                Color.fromRGB(164, 44, 214),    // Witch Dress Pale Purple
                Color.fromRGB(163, 235, 30)     // Slime Green
        );
        config.getList("pumpkin-explosion.firework-effects.types",
                Arrays.stream(FireworkEffect.Type.values()).map(Enum::name).toList(),
                """
                        FireworkEffect Types you wish to use. Has to be a valid enum from:
                        https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html
                        """
        ).forEach(effect -> {
            try {
                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effect);
                halloweenColors.forEach(primary_color -> {
                    Color secondary_color = primary_color;
                    while (secondary_color.equals(primary_color)) { // Avoid rolling the same color
                        secondary_color = halloweenColors.get(new Random().nextInt(0, halloweenColors.size()));
                    }
                    this.fireWorkEffects.add(FireworkEffect.builder()
                            .withColor(primary_color, secondary_color)
                            .with(effectType)
                            .flicker(flicker)
                            .trail(trail)
                            .build());
                });
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warning("FireworkEffect Type '"+effect+"' not recognized. " +
                        "Please use valid enums from: https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html");
            }
        });
    }

    @Override
    public boolean shouldEnable() {
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-explosion.firework-effects.enable", true);
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
    private void onPrePumpkinExplode(PostPumpkinExplodeEvent event) {
        if (!event.hasExploded()) return;
        final Location explosionLoc = event.getExplodeLocation();
        Firework firework = explosionLoc.getWorld().spawn(explosionLoc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(fireWorkEffects.get(new Random().nextInt(0, fireWorkEffects.size())));
        firework.setFireworkMeta(meta);
        firework.detonate();
    }
}