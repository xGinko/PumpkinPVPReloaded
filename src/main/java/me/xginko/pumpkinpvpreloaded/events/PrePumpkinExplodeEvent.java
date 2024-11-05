package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PrePumpkinExplodeEvent extends PumpkinExplodeEvent implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull Block pumpkin;
    private final @NotNull Player exploder;
    private final @NotNull ExplosionTrigger.TriggerAction triggerAction;
    private boolean cancelPrecedingEvent;

    public PrePumpkinExplodeEvent(
            @NotNull Block pumpkin,
            @NotNull Player exploder,
            @NotNull Location explodeLocation,
            @NotNull ExplosionTrigger.TriggerAction triggerAction
    ) {
        super(explodeLocation);
        this.pumpkin = pumpkin;
        this.exploder = exploder;
        this.triggerAction = triggerAction;
        this.isCancelled = false;
        this.cancelPrecedingEvent = false;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }

    public void setPumpkin(@NotNull Block pumpkin) {
        this.pumpkin = pumpkin;
    }

    public @NotNull Player getExploder() {
        return exploder;
    }

    public @NotNull ExplosionTrigger.TriggerAction getTriggerAction() {
        return triggerAction;
    }

    public boolean cancelPreceding() {
        return cancelPrecedingEvent;
    }

    public void setPrecedingCancelled(boolean cancel) {
        this.cancelPrecedingEvent = cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
