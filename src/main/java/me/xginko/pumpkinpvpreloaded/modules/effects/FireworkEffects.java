package me.xginko.pumpkinpvpreloaded.modules.effects;

import com.google.common.collect.ImmutableList;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.Util;
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

public class FireworkEffects extends PumpkinPVPModule implements Listener {

    private final @NotNull List<FireworkEffect> firework_effects;

    public FireworkEffects() {
        super("pumpkin-explosion.firework-effects", true);

        final List<String> defaults = Arrays.asList(
                "#FFAE03",   // Pumpkin Light Orange
                "#FE4E00",   // Pumpkin Dark Orange
                "#1A090D",   // Witch Hat Dark Purple
                "#A42CD6",   // Witch Dress Pale Purple
                "#A3EB1E"    // Slime Green
        );
        final List<Color> colors = config.getList(configPath + ".colors", defaults,
                "You need to configure at least 1 color.")
                .stream()
                .map(hexString -> {
                    try {
                        return Util.fromHexString(hexString);
                    } catch (NumberFormatException e) {
                        warn("Could not parse color '" + hexString + "'. Is it formatted correctly?");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), parsedColors -> {
                    if (parsedColors.isEmpty()) {
                        return defaults.stream().map(Util::fromHexString).collect(Collectors.toList());
                    }
                    return parsedColors;
                }));

        final boolean flicker = config.getBoolean(configPath + ".flicker", false);
        final boolean trail = config.getBoolean(configPath + ".trail", false);

        final List<FireworkEffect.Type> effectTypes = config.getList(configPath + ".types",
                Arrays.stream(FireworkEffect.Type.values()).map(Enum::name).sorted().collect(Collectors.toList()),
                "FireworkEffect Types you wish to use. Has to be a valid enum from: \n" +
                "https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html")
                .stream()
                .map(configuredType -> {
                    try {
                        return FireworkEffect.Type.valueOf(configuredType);
                    } catch (IllegalArgumentException e) {
                        warn("FireworkEffect Type '" + configuredType + "' not recognized. " +
                             "Please use valid enums from: https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html");
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.collectingAndThen(Collectors.toList(), parsedTypes -> {
                    if (parsedTypes.isEmpty()) {
                        return Arrays.asList(FireworkEffect.Type.values());
                    }
                    return parsedTypes;
                }));

        final List<FireworkEffect> parsedFireworkEffects = new ArrayList<>();

        for (FireworkEffect.Type effectType : effectTypes) {
            for (Color primaryColor : colors) {
                if (colors.size() == 1) {
                    parsedFireworkEffects.add(FireworkEffect.builder()
                            .withColor(primaryColor)
                            .with(effectType)
                            .flicker(flicker)
                            .trail(trail)
                            .build());
                    continue;
                }
                Color secondaryColor;
                do {
                    secondaryColor = colors.get(Util.RANDOM.nextInt(colors.size()));
                } while (secondaryColor.equals(primaryColor)); // Ensure we never combine the same colors
                parsedFireworkEffects.add(FireworkEffect.builder()
                        .withColor(primaryColor, secondaryColor)
                        .with(effectType)
                        .flicker(flicker)
                        .trail(trail)
                        .build());
            }
        }

        this.firework_effects = ImmutableList.copyOf(parsedFireworkEffects);
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    private FireworkEffect getRandomEffect() {
        return this.firework_effects.get(Util.RANDOM.nextInt(this.firework_effects.size()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinBlockExplode(PostPumpkinBlockExplodeEvent event) {
        onPostPumpkinExplode(event);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinEntityExplode(PostPumpkinEntityExplodeEvent event) {
        onPostPumpkinExplode(event);
    }

    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            Firework firework = event.getLocation().getWorld().spawn(event.getLocation(), Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.clearEffects();
            meta.addEffect(this.getRandomEffect());
            firework.setFireworkMeta(meta);
            firework.setSilent(true);
            firework.detonate();
        }
    }
}