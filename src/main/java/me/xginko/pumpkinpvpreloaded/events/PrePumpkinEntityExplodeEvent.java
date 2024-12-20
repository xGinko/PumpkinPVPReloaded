package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinEntityExplodeEvent extends PrePumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private @NotNull Entity entity;

    public PrePumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull Entity entity,
            @Nullable Player exploder,
            @NotNull Location explodeLocation
    ) {
        super(triggerAction, exploder, explodeLocation);
        this.entity = entity;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }

    public void setEntity(@NotNull Entity entity) {
        this.entity = entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
