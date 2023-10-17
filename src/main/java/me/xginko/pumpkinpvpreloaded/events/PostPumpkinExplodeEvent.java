package me.xginko.pumpkinpvpreloaded.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class PostPumpkinExplodeEvent extends Event {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Player exploder;
    private final @NotNull Collection<Player> likelyDamagedPlayers;
    private final @NotNull Location explosionLocation;
    private final float explosionPower;
    private final boolean setFire, destroyedBlocks;

    public PostPumpkinExplodeEvent(
            final @NotNull Player exploder,
            final @NotNull Location explosionLocation,
            final @NotNull Collection<Player> likelyDamagedPlayers,
            final float explosionPower,
            final boolean setFire,
            final boolean destroyedBlocks
    ) {
        this.exploder = exploder;
        this.explosionLocation = explosionLocation;
        this.likelyDamagedPlayers = likelyDamagedPlayers;
        this.explosionPower = explosionPower;
        this.setFire = setFire;
        this.destroyedBlocks = destroyedBlocks;
    }

    public @NotNull Player getExploder() {
        return exploder;
    }
    public @NotNull Location getExplosionLocation() {
        return explosionLocation;
    }
    public @NotNull Collection<Player> getLikelyDamagedPlayers() {
        return likelyDamagedPlayers;
    }
    public float getExplosionPower() {
        return explosionPower;
    }
    public boolean isSetFire() {
        return setFire;
    }
    public boolean isDestroyedBlocks() {
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
