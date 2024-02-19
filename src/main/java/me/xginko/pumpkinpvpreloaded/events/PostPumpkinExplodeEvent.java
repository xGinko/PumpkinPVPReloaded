package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.enums.TriggerAction;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PostPumpkinExplodeEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Player exploder;
    private final @NotNull Location explosionLocation;
    private final float explosionPower;
    private final boolean setFire, destroyedBlocks, hasExploded;
    private final @NotNull TriggerAction triggerAction;

    public PostPumpkinExplodeEvent(
            final @NotNull Player exploder,
            final @NotNull Location explosionLocation,
            final float explosionPower,
            final boolean setFire,
            final boolean destroyedBlocks,
            final @NotNull TriggerAction triggerAction,
            final boolean hasExploded
    ) {
        this.hasExploded = hasExploded;
        this.exploder = exploder;
        this.explosionLocation = explosionLocation;
        this.explosionPower = explosionPower;
        this.setFire = setFire;
        this.destroyedBlocks = destroyedBlocks;
        this.triggerAction = triggerAction;
    }

    public @NotNull Player getExploder() {
        return exploder;
    }
    public @NotNull Location getExplodeLocation() {
        return explosionLocation;
    }
    public boolean hasExploded() {
        return hasExploded;
    }
    public float getExplodePower() {
        return explosionPower;
    }
    public boolean hasSetFire() {
        return setFire;
    }
    public boolean hasDestroyedBlocks() {
        return destroyedBlocks;
    }
    public @NotNull TriggerAction getTriggerAction() {
        return triggerAction;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
