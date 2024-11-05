package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinBlockExplodeEvent extends PrePumpkinExplodeEvent implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull Block pumpkin;
    private boolean cancelPrecedingEvent;

    public PrePumpkinBlockExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull Block pumpkin,
            @Nullable Player exploder,
            @NotNull Location explodeLocation
    ) {
        super(triggerAction, exploder, explodeLocation);
        this.pumpkin = pumpkin;
        this.isCancelled = false;
        this.cancelPrecedingEvent = false;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }

    public void setPumpkin(@NotNull Block pumpkin) {
        this.pumpkin = pumpkin;
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
