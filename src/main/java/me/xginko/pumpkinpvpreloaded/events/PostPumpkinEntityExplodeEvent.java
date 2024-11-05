package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostPumpkinEntityExplodeEvent extends PostPumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Entity entity;

    public PostPumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.Action triggerAction,
            @NotNull Entity entity,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explosionPower, boolean setFire, boolean destroyedBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explodeLocation, explosionPower, setFire, destroyedBlocks, hasExploded);
        this.entity = entity;
    }

    public @NotNull Entity getEntity() {
        return entity;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
