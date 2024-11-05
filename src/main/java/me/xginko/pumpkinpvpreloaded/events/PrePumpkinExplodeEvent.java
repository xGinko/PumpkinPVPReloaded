package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PrePumpkinExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    public PrePumpkinExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explodePower, boolean setFire, boolean breakBlocks
    ) {
        super(triggerAction, exploder, explodeLocation, explodePower, setFire, breakBlocks);
    }

    public PrePumpkinExplodeEvent(ExplosionTrigger.TriggerAction triggerAction, @Nullable Player exploder, @NotNull Location explodeLocation) {
        super(triggerAction, exploder, explodeLocation);
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
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
