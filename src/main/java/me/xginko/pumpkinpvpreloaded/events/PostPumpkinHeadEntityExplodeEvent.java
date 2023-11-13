package me.xginko.pumpkinpvpreloaded.events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostPumpkinHeadEntityExplodeEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull LivingEntity pumpkinHead;
    private final @Nullable Player killer;
    private final @NotNull Location explodeLocation;
    private final float explodePower;
    private final boolean setFire, destroyedBlocks, hasExploded;

    public PostPumpkinHeadEntityExplodeEvent(
            @NotNull LivingEntity pumpkinHead,
            final @Nullable Player killer,
            @NotNull Location explodeLocation,
            final float explosionPower,
            final boolean setFire,
            final boolean destroyedBlocks
    ) {
        this.pumpkinHead = pumpkinHead;
        this.killer = killer;
        this.explodeLocation = explodeLocation;
        this.explodePower = explosionPower;
        this.setFire = setFire;
        this.destroyedBlocks = destroyedBlocks;
        this.hasExploded = explodeLocation.getWorld().createExplosion(explodeLocation, explodePower, setFire, destroyedBlocks);
    }

    public @NotNull LivingEntity getPumpkinHeadEntity() {
        return pumpkinHead;
    }
    public @Nullable Player getKiller() {
        return killer;
    }
    public @NotNull Location getExplodeLocation() {
        return explodeLocation;
    }
    public boolean hasExploded() {
        return hasExploded;
    }
    public float getExplodePower() {
        return explodePower;
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
