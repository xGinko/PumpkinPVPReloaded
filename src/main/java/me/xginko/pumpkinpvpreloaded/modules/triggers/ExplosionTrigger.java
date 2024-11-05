package me.xginko.pumpkinpvpreloaded.modules.triggers;

import com.cryptomorin.xseries.XMaterial;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.events.PumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PumpkinEntityExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinBlockExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinExplodeEvent;
import me.xginko.pumpkinpvpreloaded.events.PrePumpkinEntityExplodeEvent;
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

    protected void doPumpkinExplosion(PrePumpkinExplodeEvent prePumpkinExplosion) {
        if (PumpkinPVPReloaded.isServerFolia()) {
            scheduling.regionSpecificScheduler(prePumpkinExplosion.getLocation())
                    .run(() -> createExplosionWithEvent(prePumpkinExplosion));
        } else {
            createExplosionWithEvent(prePumpkinExplosion);
        }
    }

    private void createExplosionWithEvent(PrePumpkinExplodeEvent prePumpkinExplosion) {
        if (prePumpkinExplosion.getTriggerAction() == TriggerAction.PUMPKIN_HEAD_ENTITY_DEATH) {
            PrePumpkinEntityExplodeEvent prePumpkinEntityExplosion = (PrePumpkinEntityExplodeEvent) prePumpkinExplosion;
            plugin.getServer().getPluginManager().callEvent(new PumpkinEntityExplodeEvent(
                    triggerAction,
                    prePumpkinEntityExplosion.getEntity(),
                    prePumpkinEntityExplosion.getExploder(),
                    prePumpkinEntityExplosion.getLocation(),
                    prePumpkinEntityExplosion.getPower(),
                    prePumpkinEntityExplosion.getFire(),
                    prePumpkinEntityExplosion.getBreakBlocks(),
                    prePumpkinEntityExplosion.getLocation().getWorld().createExplosion(
                            prePumpkinEntityExplosion.getEntity(),
                            prePumpkinEntityExplosion.getLocation(),
                            prePumpkinEntityExplosion.getPower(),
                            prePumpkinEntityExplosion.getFire(),
                            prePumpkinEntityExplosion.getBreakBlocks()
                    )
            ));
        } else {
            PrePumpkinBlockExplodeEvent prePumpkinBlockExplosion = (PrePumpkinBlockExplodeEvent) prePumpkinExplosion;
            prePumpkinBlockExplosion.getPumpkin().setType(XMaterial.AIR.parseMaterial(), false);
            plugin.getServer().getPluginManager().callEvent(new PumpkinBlockExplodeEvent(
                    triggerAction,
                    prePumpkinBlockExplosion.getPumpkin(),
                    prePumpkinBlockExplosion.getExploder(),
                    prePumpkinBlockExplosion.getLocation(),
                    prePumpkinBlockExplosion.getPower(),
                    prePumpkinBlockExplosion.getFire(),
                    prePumpkinBlockExplosion.getBreakBlocks(),
                    prePumpkinBlockExplosion.getLocation().getWorld().createExplosion(
                            prePumpkinBlockExplosion.getLocation(),
                            prePumpkinBlockExplosion.getPower(),
                            prePumpkinBlockExplosion.getFire(),
                            prePumpkinBlockExplosion.getBreakBlocks()
                    )
            ));
        }
    }

    public enum TriggerAction {
        PUMPKIN_HEAD_ENTITY_DEATH,
        LEFT_CLICK_PUMPKIN,
        RIGHT_CLICK_PUMPKIN,
        PLACE_PUMPKIN,
        SHEAR_PUMPKIN
    }
}
