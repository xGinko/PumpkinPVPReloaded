package me.xginko.pumpkinpvpreloaded.modules.effects;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class FireworkEffects implements PumpkinPVPModule, Listener {

    private final @NotNull List<FireworkEffect> firework_effects;

    public FireworkEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        final List<String> defaults = List.of(
                "FFAE03",   // Pumpkin Light Orange
                "FE4E00",   // Pumpkin Dark Orange
                "1A090D",   // Witch Hat Dark Purple
                "A42CD6",   // Witch Dress Pale Purple
                "A3EB1E"    // Slime Green
        );
        List<String> configuredColors = config.getList("pumpkin-explosion.firework-effects.colors", defaults,
                "You need to configure at least 1 color.");
        if (configuredColors.isEmpty()) {
            PumpkinPVPReloaded.getLog().warn("You did not configure any colors. Falling back to defaults.");
            configuredColors = defaults;
        }
        final List<Color> colors = configuredColors.stream().map(hexString -> {
            try {
                final String parseable = hexString.replaceAll("#", "");
                return Color.fromRGB(
                        Integer.parseInt(parseable.substring(0, 2), 16),
                        Integer.parseInt(parseable.substring(2, 4), 16),
                        Integer.parseInt(parseable.substring(4, 6), 16)
                );
            } catch (NumberFormatException e) {
                PumpkinPVPReloaded.getLog().warn("Could not parse color '" + hexString + "'. Is it formatted correctly?");
                return null;
            }
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());
        if (colors.isEmpty()) {
            PumpkinPVPReloaded.getLog().warn("Could not parse any color. Using defaults.");
            colors.add(Color.fromRGB(255, 174, 3));
            colors.add(Color.fromRGB(254, 78, 0));
            colors.add(Color.fromRGB(26, 9, 13));
            colors.add(Color.fromRGB(164, 44, 214));
            colors.add(Color.fromRGB(163, 235, 30));
        }

        final boolean flicker = config.getBoolean("pumpkin-explosion.firework-effects.flicker", false);
        final boolean trail = config.getBoolean("pumpkin-explosion.firework-effects.trail", false);

        List<FireworkEffect> parsedFireworkEffects = new ArrayList<>();
        config.getList("pumpkin-explosion.firework-effects.types",
                Arrays.stream(FireworkEffect.Type.values()).map(Enum::name).sorted().toList(), """
                        FireworkEffect Types you wish to use. Has to be a valid enum from:\s
                        https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html"""
        ).forEach(effect -> {
            try {
                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effect);
                colors.forEach(primaryColor -> {
                    if (colors.size() == 1) {
                        parsedFireworkEffects.add(FireworkEffect.builder()
                                .withColor(primaryColor)
                                .with(effectType)
                                .flicker(flicker)
                                .trail(trail)
                                .build());
                        return;
                    }
                    Color secondaryColor;
                    do {
                        secondaryColor = colors.get(PumpkinPVPReloaded.getRandom().nextInt(colors.size()));
                    } while (secondaryColor.equals(primaryColor)); // Avoid rolling the same color
                    parsedFireworkEffects.add(FireworkEffect.builder()
                            .withColor(primaryColor, secondaryColor)
                            .with(effectType)
                            .flicker(flicker)
                            .trail(trail)
                            .build());
                });
            } catch (IllegalArgumentException e) {
                PumpkinPVPReloaded.getLog().warn("FireworkEffect Type '"+effect+"' not recognized. " +
                        "Please use valid enums from: https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html");
            }
        });
        this.firework_effects = parsedFireworkEffects.stream().distinct().toList();
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

    private FireworkEffect getRandomEffect() {
        return this.firework_effects.get(PumpkinPVPReloaded.getRandom().nextInt(this.firework_effects.size()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            Firework firework = event.getExplodeLocation().getWorld().spawn(event.getExplodeLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.clearEffects();
            meta.addEffect(this.getRandomEffect());
            firework.setFireworkMeta(meta);
            firework.setSilent(true);
            firework.detonate();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinHeadExplode(PostPumpkinHeadEntityExplodeEvent event) {
        if (event.hasExploded()) {
            Firework firework = event.getExplodeLocation().getWorld().spawn(event.getExplodeLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.clearEffects();
            meta.addEffect(this.getRandomEffect());
            firework.setFireworkMeta(meta);
            firework.setSilent(true);
            firework.detonate();
        }
    }
}