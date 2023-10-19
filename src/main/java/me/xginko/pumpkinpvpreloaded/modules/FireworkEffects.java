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

    private final List<FireworkEffect> effectList = new ArrayList<>();

    protected FireworkEffects() {
        shouldEnable();
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        final boolean trail_enabled = config.getBoolean("pumpkin-explosion.firework-effects.trails", false);
        final boolean flicker_enabled = config.getBoolean("pumpkin-explosion.firework-effects.flicker", false);
        config.getList("pumpkin-explosion.firework-effects.types",
                Arrays.stream(FireworkEffect.Type.values()).map(Enum::name).toList(),
                """
                        FireworkEffect Types you wish to use. Has to be a valid enum from:
                        https://jd.papermc.io/paper/1.20/org/bukkit/FireworkEffect.Type.html
                        """
        ).forEach(effect -> {
            try {
                FireworkEffect.Type effectType = FireworkEffect.Type.valueOf(effect);
                // Pumpkin Light Orange
                effectList.add(FireworkEffect.builder().with(effectType).trail(trail_enabled).flicker(flicker_enabled)
                        .withColor(Color.fromRGB(255, 174, 3)).build());
                // Pumpkin Dark Orange
                effectList.add(FireworkEffect.builder().with(effectType).trail(trail_enabled).flicker(flicker_enabled)
                        .withColor(Color.fromRGB(254, 78, 0)).build());
                // Witch Hat Dark Purple
                effectList.add(FireworkEffect.builder().with(effectType).trail(trail_enabled).flicker(flicker_enabled)
                        .withColor(Color.fromRGB(26, 9, 13)).build());
                // Witch Dress Pale Purple
                effectList.add(FireworkEffect.builder().with(effectType).trail(trail_enabled).flicker(flicker_enabled)
                        .withColor(Color.fromRGB(164, 44, 214)).build());
                // Slime Green
                effectList.add(FireworkEffect.builder().with(effectType).trail(trail_enabled).flicker(flicker_enabled)
                        .withColor(Color.fromRGB(163, 235, 30)).build());
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
        final Location explosionLoc = event.getExplosionLocation();
        Firework firework = explosionLoc.getWorld().spawn(explosionLoc, Firework.class);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.clearEffects();
        meta.addEffect(effectList.get(new Random().nextInt(effectList.size())));
        firework.setFireworkMeta(meta);
        firework.detonate();
    }
}