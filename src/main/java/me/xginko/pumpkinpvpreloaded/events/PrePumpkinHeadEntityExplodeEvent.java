package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinHeadEntityExplodeEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull LivingEntity pumpkinHead;
    private final @Nullable Player killer;
    private @NotNull Location explodeLocation;
    private float explodePower;
    private boolean setFire, breakBlocks;

    public PrePumpkinHeadEntityExplodeEvent(
            @NotNull LivingEntity pumpkinHead,
            final @Nullable Player killer,
            @NotNull Location explodeLocation
    ) {
        this.pumpkinHead = pumpkinHead;
        this.killer = killer;
        this.explodeLocation = explodeLocation;
        PumpkinPVPConfig config = PumpkinPVPReloaded.config();
        this.explodePower = config.explosion_power;
        this.setFire = config.explosion_set_fire;
        this.breakBlocks = config.explosion_break_blocks;
        this.isCancelled = false;
    }

    public @NotNull LivingEntity getPumpkinHeadEntity() {
        return pumpkinHead;
    }
    public void setPumpkinHeadEntity(@NotNull LivingEntity pumpkinHead) {
        this.pumpkinHead = pumpkinHead;
    }
    public @Nullable Player getKiller() {
        return killer;
    }
    public @NotNull Location getExplodeLocation() {
        return explodeLocation;
    }
    public void setExplodeLocation(@NotNull Location explodeLocation) {
        this.explodeLocation = explodeLocation;
    }
    public float getExplodePower() {
        return explodePower;
    }
    public void setExplodePower(float explodePower) {
        this.explodePower = explodePower;
    }
    public boolean shouldSetFire() {
        return setFire;
    }
    public void setFire(boolean setFire) {
        this.setFire = setFire;
    }
    public boolean shouldBreakBlocks() {
        return breakBlocks;
    }
    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
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
