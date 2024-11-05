package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PumpkinEntityExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Entity entity;
    private final boolean hasExploded;

    public PumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull Entity entity,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explosionPower, boolean setFire, boolean destroyedBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explodeLocation, explosionPower, setFire, destroyedBlocks);
        this.entity = entity;
        this.hasExploded = hasExploded;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }

    public boolean hasExploded() {
        return hasExploded;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
