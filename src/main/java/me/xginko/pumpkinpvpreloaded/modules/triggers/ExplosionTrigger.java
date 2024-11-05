package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PostPumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.modules.PumpkinPVPModule;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public abstract class ExplosionTrigger extends PumpkinPVPModule implements Listener {

    public final TriggerAction triggerAction;

    public ExplosionTrigger(TriggerAction triggerAction, String configPath, boolean defEnabled, String comment) {
        super(configPath, defEnabled, comment);
        this.triggerAction = triggerAction;
    }

    public ExplosionTrigger(TriggerAction triggerAction, String configPath, boolean defEnabled) {
        this(triggerAction, configPath, defEnabled, null);
    }

    @Override
    public void enable() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

    protected void doPumpkinExplosion(PrePumpkinExplodeEvent prePumpkinExplodeEvent) {
        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(prePumpkinExplodeEvent.getExplodeLocation())
                    .run(() -> createExplosionWithEvent(prePumpkinExplodeEvent));
        } else {
            createExplosionWithEvent(prePumpkinExplodeEvent);
        }
    }

    private void createExplosionWithEvent(PrePumpkinExplodeEvent prePumpkinExplodeEvent) {
        prePumpkinExplodeEvent.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);

        plugin.getServer().getPluginManager().callEvent(new PostPumpkinExplodeEvent(
                prePumpkinExplodeEvent.getExploder(),
                prePumpkinExplodeEvent.getExplodeLocation(),
                prePumpkinExplodeEvent.getPower(),
                prePumpkinExplodeEvent.getFire(),
                prePumpkinExplodeEvent.getBreakBlocks(),
                triggerAction,
                prePumpkinExplodeEvent.getExplodeLocation().getWorld().createExplosion(
                        prePumpkinExplodeEvent.getExplodeLocation(),
                        prePumpkinExplodeEvent.getPower(),
                        prePumpkinExplodeEvent.getFire(),
                        prePumpkinExplodeEvent.getBreakBlocks()
                )
        ));
    }

    public enum TriggerAction {
        PUMPKIN_HEAD,
        LEFT_CLICK,
        RIGHT_CLICK,
        BLOCK_PLACE,
        SHEAR
    }
}
