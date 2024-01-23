package me.xginko.pumpkinpvpreloaded.modules;

import me.xginko.pumpkinpvpreloaded.modules.effects.DeathSoundEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.ExplodeSoundEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.FireworkEffects;
import me.xginko.pumpkinpvpreloaded.modules.effects.LightningEffects;
import me.xginko.pumpkinpvpreloaded.modules.triggers.*;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.RateLimitPumpkinExplosions;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.RequireBaseBlocks;
import me.xginko.pumpkinpvpreloaded.modules.mechanics.EnablePerWorld;

import java.util.HashSet;

public interface PumpkinPVPModule {

    boolean shouldEnable();
    void enable();
    void disable();

    HashSet<PumpkinPVPModule> modules = new HashSet<>();

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

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
