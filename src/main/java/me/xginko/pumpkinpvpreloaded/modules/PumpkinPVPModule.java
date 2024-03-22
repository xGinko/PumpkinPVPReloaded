package me.xginko.pumpkinpvpreloaded.modules;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.modules.effects.DeathSoundEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.ExplodeSoundEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.FireworkEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.LightningEffects;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.EnablePerWorld;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.RateLimitPumpkinExplosions;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.RequireBaseBlocks;
import me.xginko.pumpkinpvpreloaded.modules.triggers.*;
import me.xginko.pumpkinpvpreloaded.utils.ColorUtil;
import net.kyori.adventure.text.Component;

import java.util.HashSet;
import java.util.Set;

public interface PumpkinPVPModule {

    String configPath();
    boolean shouldEnable();
    void enable();
    void disable();

    Set<PumpkinPVPModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(PumpkinPVPModule::disable);
        modules.clear();

        modules.add(new ExplodePumpkinOnShear());
        modules.add(new ExplodePumpkinOnLeftClick());
        modules.add(new ExplodePumpkinOnRightClick());
        modules.add(new ExplodePumpkinOnPlace());
        modules.add(new ExplodePumpkinHeadEntities());

        modules.add(new ExplodeSoundEffects());
        modules.add(new FireworkEffects());
        modules.add(new LightningEffects());
        modules.add(new DeathSoundEffects());

        modules.add(new RateLimitPumpkinExplosions());
        modules.add(new RequireBaseBlocks());
        modules.add(new EnablePerWorld());

        modules.add(new AdjustDamageInfo());

        for (PumpkinPVPModule module : modules) {
            if (module.shouldEnable()) module.enable();
        }
    }

    default void trace(String message, Throwable throwable) {
        PumpkinPVPReloaded.getPrefixedLogger().trace(logPrefix() + message, throwable);
    }

    default void error(String message) {
        PumpkinPVPReloaded.getPrefixedLogger().error(logPrefix() + message);
    }

    default void warn(String message) {
        PumpkinPVPReloaded.getPrefixedLogger().warn(Component.text(logPrefix() + message).color(ColorUtil.ORANGE));
    }

    default void info(String message) {
        PumpkinPVPReloaded.getPrefixedLogger().info(Component.text(logPrefix() + message).color(ColorUtil.GREEN));
    }

    default String logPrefix() {
        String[] split = configPath().split("\\.");
        if (split.length <= 2) return "<" + configPath() + "> ";
        return "<" + String.join(".", split[split.length - 2], split[split.length - 1]) + "> ";
    }
}
