package me.xginko.pumpkinpvpreloaded.modules.effects;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinHeadEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class FireworkEffects implements PumpkinPVPModule, Listener {

    private final @NotNull List<FireworkEffect> firework_effects;

    public FireworkEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        final List<String> defaults = List.of(
                "<color:#FFAE03>",   // Pumpkin Light Orange
                "<color:#FE4E00>",   // Pumpkin Dark Orange
                "<color:#1A090D>",   // Witch Hat Dark Purple
                "<color:#A42CD6>",   // Witch Dress Pale Purple
                "<color:#A3EB1E>"    // Slime Green
        );
        List<String> configuredColors = config.getList("pumpkin-explosion.firework-effects.colors", defaults,
                "You need to configure at least 1 color.");
        if (configuredColors.isEmpty()) {
            PumpkinPVPReloaded.getLog().warn("You did not configure any colors. Falling back to defaults.");
            configuredColors = defaults;
        }
        final List<Color> colors = configuredColors.stream().map(serialized -> {
            TextColor textColor = MiniMessage.miniMessage().deserialize(serialized).color();
            if (textColor == null) {
                PumpkinPVPReloaded.getLog().warn("Hex color string '"+serialized+"' is not formatted correctly. " +
                        "The format is as follows: <color:#E54264>");
                return null;
            }
            return Color.fromRGB(textColor.red(), textColor.green(), textColor.blue());
        }).filter(Objects::nonNull).distinct().toList();

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
            firework.detonate();
        }
    }
}