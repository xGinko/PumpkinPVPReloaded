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
    private boolean has_enough_colors = true;

    public FireworkEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        List<String> configuredColors = config.getList("pumpkin-explosion.firework-effects.colors", List.of(
                "<color:#FFAE03>",   // Pumpkin Light Orange
                "<color:#FE4E00>",   // Pumpkin Dark Orange
                "<color:#1A090D>",   // Witch Hat Dark Purple
                "<color:#A42CD6>",   // Witch Dress Pale Purple
                "<color:#A3EB1E>"    // Slime Green
        ), "You need to configure at least 2 colors.");
        if (configuredColors.size() < 2) {
            PumpkinPVPReloaded.getLog().severe("You need to configure at least 2 colors. Disabling firework effects.");
            has_enough_colors = false;
        }
        List<Color> parsedColors = new ArrayList<>();
        configuredColors.forEach(hexString -> {
            TextColor textColor = MiniMessage.miniMessage().deserialize(hexString).color();
            if (textColor == null) {
                PumpkinPVPReloaded.getLog().warning("Hex color string '"+hexString+"' is not formatted correctly. Use it like this: <color:#E54264>");
                return;
            }
            parsedColors.add(Color.fromRGB(textColor.red(), textColor.green(), textColor.blue()));
        });
        final boolean flicker = config.getBoolean("pumpkin-explosion.firework-effects.flicker", false);
        final boolean trail = config.getBoolean("pumpkin-explosion.firework-effects.trail", false);
        config.getList("pumpkin-explosion.firework-effects.types",
                Arrays.stream(FireworkEffect.Type.values()).map(Enum::name).toList(),
                """
                        FireworkEffect Types you wish to use. Has to be a valid enum from:
                        https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html
                        """
        ).forEach(effect -> {
            try {
                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effect);
                parsedColors.forEach(primary_color -> {
                    Color secondary_color = primary_color;
                    int tries = 0;
                    while (secondary_color.equals(primary_color)) { // Avoid rolling the same color
                        if (tries > 100) break; // Avoid infinite loop on bad config
                        secondary_color = parsedColors.get(new Random().nextInt(parsedColors.size()));
                        tries++;
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
        return PumpkinPVPReloaded.getConfiguration().getBoolean("pumpkin-explosion.firework-effects.enable", true)
                && has_enough_colors;
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
        return this.fireWorkEffects.get(new Random().nextInt(this.fireWorkEffects.size()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onPostPumpkinExplode(PostPumpkinExplodeEvent event) {
        if (event.hasExploded()) {
            final Location explosionLoc = event.getExplodeLocation();
            Firework firework = explosionLoc.getWorld().spawn(explosionLoc, Firework.class);
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
            final Location explosionLoc = event.getExplodeLocation();
            Firework firework = explosionLoc.getWorld().spawn(explosionLoc, Firework.class);
            FireworkMeta meta = firework.getFireworkMeta();
            meta.clearEffects();
            meta.addEffect(this.getRandomEffect());
            firework.setFireworkMeta(meta);
            firework.detonate();
        }
    }
}