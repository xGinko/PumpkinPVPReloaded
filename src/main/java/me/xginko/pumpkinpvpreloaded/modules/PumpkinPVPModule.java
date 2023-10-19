package me.xginko.pumpkinpvpreloaded.modules;

import java.util.HashSet;

public interface PumpkinPVPModule {

    boolean shouldEnable();
    void enable();
    void disable();

    HashSet<PumpkinPVPModule> modules = new HashSet<>();

    static void reloadModules() {
        modules.forEach(PumpkinPVPModule::disable);
        modules.clear();

        modules.add(new AdjustDamageInfo());
        modules.add(new ExplodePumpkinOnShear());
        modules.add(new ExplodePumpkinOnClick());
        modules.add(new ExplodePumpkinOnPlace());
        modules.add(new RateLimitPumpkinExplosions());
        modules.add(new RequireBaseBlocks());
        modules.add(new FireworkEffects());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
