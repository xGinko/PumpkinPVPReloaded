package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public abstract class PumpkinExplodeEvent extends Event {

    private @NotNull Location explodeLocation;
    private float explodePower;
    private boolean setFire, breakBlocks;

    public PumpkinExplodeEvent(@NotNull Location explodeLocation, float explodePower, boolean setFire, boolean breakBlocks) {
        this.explodeLocation = explodeLocation;
        this.explodePower = explodePower;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }

    public PumpkinExplodeEvent(@NotNull Location explodeLocation) {
        this(
                explodeLocation,
                PumpkinPVPReloaded.config().explosion_power,
                PumpkinPVPReloaded.config().explosion_set_fire,
                PumpkinPVPReloaded.config().explosion_break_blocks
        );
    }

    public @NotNull Location getExplodeLocation() {
        return explodeLocation;
    }

    public void setExplodeLocation(@NotNull Location explodeLocation) {
        this.explodeLocation = explodeLocation;
    }

    public float getPower() {
        return explodePower;
    }

    public void setPower(float explodePower) {
        this.explodePower = explodePower;
    }

    public boolean getFire() {
        return setFire;
    }

    public void setFire(boolean setFire) {
        this.setFire = setFire;
    }

    public boolean getBreakBlocks() {
        return breakBlocks;
    }

    public void setBreakBlocks(boolean breakBlocks) {
        this.breakBlocks = breakBlocks;
    }
}
