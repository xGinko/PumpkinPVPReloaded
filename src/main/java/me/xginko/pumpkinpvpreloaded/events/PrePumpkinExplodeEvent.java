package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPConfig;
import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PrePumpkinExplodeEvent extends Event implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull Block pumpkin;
    private final @NotNull Player exploder;
    private @NotNull Location explodeLocation;
    private float explodePower;
    private boolean setFire;
    private boolean breakBlocks;

    public PrePumpkinExplodeEvent(@NotNull final Block pumpkin, final @NotNull Player exploder, @NotNull Location explodeLocation) {
        this.pumpkin = pumpkin;
        this.exploder = exploder;
        this.explodeLocation = explodeLocation;
        PumpkinPVPConfig config = PumpkinPVPReloaded.getConfiguration();
        this.explodePower = config.explosion_power;
        this.setFire = config.explosion_set_fire;
        this.breakBlocks = config.explosion_break_blocks;
        this.isCancelled = false;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }
    public void setPumpkin(@NotNull Block pumpkin) {
        this.pumpkin = pumpkin;
    }
    public @NotNull Player getExploder() {
        return exploder;
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
