package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import me.xginko.pumpkinpvpreloaded.utils.TriggerAction;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ExplosionTrigger extends PumpkinPVPModule implements Listener {

    public ExplosionTrigger(String configPath, boolean defEnabled) {
        super(configPath, defEnabled);
    }

    public ExplosionTrigger(String configPath, boolean defEnabled, String comment) {
        super(configPath, defEnabled, comment);
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    protected void doPumpkinExplosion(TriggerAction action, PrePumpkinExplodeEvent prePumpkinExplodeEvent) {
        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(prePumpkinExplodeEvent.getExplodeLocation())
                    .run(() -> createExplosionWithEvent(action, prePumpkinExplodeEvent));
        } else {
            createExplosionWithEvent(action, prePumpkinExplodeEvent);
        }
    }

    private void createExplosionWithEvent(TriggerAction action, PrePumpkinExplodeEvent prePumpkinExplodeEvent) {
        prePumpkinExplodeEvent.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);

        plugin.getServer().getPluginManager().callEvent(new PostPumpkinExplodeEvent(
                prePumpkinExplodeEvent.getExploder(),
                prePumpkinExplodeEvent.getExplodeLocation(),
                prePumpkinExplodeEvent.getExplodePower(),
                prePumpkinExplodeEvent.shouldSetFire(),
                prePumpkinExplodeEvent.shouldBreakBlocks(),
                action,
                prePumpkinExplodeEvent.getExplodeLocation().getWorld().createExplosion(
                        prePumpkinExplodeEvent.getExplodeLocation(),
                        prePumpkinExplodeEvent.getExplodePower(),
                        prePumpkinExplodeEvent.shouldSetFire(),
                        prePumpkinExplodeEvent.shouldBreakBlocks()
                )
        ));
    }
}
