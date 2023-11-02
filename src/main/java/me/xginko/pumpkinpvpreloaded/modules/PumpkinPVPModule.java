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
        modules.add(new DeathSoundEffects());
        modules.add(new ExplodePumpkinOnShear());
        modules.add(new ExplodePumpkinOnLeftClick());
        modules.add(new ExplodePumpkinOnRightClick());
        modules.add(new ExplodePumpkinOnPlace());
        modules.add(new RateLimitPumpkinExplosions());
        modules.add(new RequireBaseBlocks());
        modules.add(new FireworkEffects());
        modules.add(new LightningEffects());
        modules.add(new WhitelistedWorlds());

        modules.forEach(module -> {
            if (module.shouldEnable()) module.enable();
        });
    }
}
