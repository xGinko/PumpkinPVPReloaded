package me.xginko.pumpkinpvpreloaded.events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostPumpkinHeadEntityExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull LivingEntity pumpkinHead;
    private final @Nullable Player killer;
    private final boolean hasExploded;

    public PostPumpkinHeadEntityExplodeEvent(
            @NotNull LivingEntity pumpkinHead,
            final @Nullable Player killer,
            @NotNull Location explodeLocation,
            final float explosionPower,
            final boolean setFire,
            final boolean destroyedBlocks,
            final boolean hasExploded
    ) {
        super(explodeLocation, explosionPower, setFire, destroyedBlocks);
        this.hasExploded = hasExploded;
        this.pumpkinHead = pumpkinHead;
        this.killer = killer;
    }

    public @NotNull LivingEntity getPumpkinHeadEntity() {
        return pumpkinHead;
    }

    public @Nullable Player getKiller() {
        return killer;
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
