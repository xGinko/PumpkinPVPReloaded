package me.xginko.pumpkinpvpreloaded.events;

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

    public PostPumpkinExplodeEvent(
            final @NotNull Player exploder,
            final @NotNull Location explosionLocation,
            final float explosionPower,
            final boolean setFire,
            final boolean destroyedBlocks,
            final boolean hasExploded
    ) {
        this.exploder = exploder;
        this.explosionLocation = explosionLocation;
        this.explosionPower = explosionPower;
        this.setFire = setFire;
        this.destroyedBlocks = destroyedBlocks;
        this.hasExploded = hasExploded;
    }

    public @NotNull Player getExploder() {
        return exploder;
    }
    public @NotNull Location getExplosionLocation() {
        return explosionLocation;
    }
    public boolean hasExploded() {
        return hasExploded;
    }
    public float getExplosionPower() {
        return explosionPower;
    }
    public boolean hasSetFire() {
        return setFire;
    }
    public boolean hasDestroyedBlocks() {
        return destroyedBlocks;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }
    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
