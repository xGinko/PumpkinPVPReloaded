package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PrePumpkinExplodeEvent extends PumpkinExplodeEvent implements Cancellable {

    private boolean isCancelled;

    public PrePumpkinExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explodePower, boolean setFire, boolean breakBlocks
    ) {
        super(triggerAction, exploder, explodeLocation, explodePower, setFire, breakBlocks);
        this.isCancelled = false;
    }

    public PrePumpkinExplodeEvent(ExplosionTrigger.TriggerAction triggerAction, @Nullable Player exploder, @NotNull Location explodeLocation) {
        super(triggerAction, exploder, explodeLocation);
        this.isCancelled = false;
    }

    public void setLocation(@NotNull Location explodeLocation) {
        this.explodeLocation = explodeLocation;
    }

    public void setPower(float explodePower) {
        this.explodePower = explodePower;
    }

    public void setFire(boolean setFire) {
        this.setFire = setFire;
    }

    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }
}
