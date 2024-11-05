package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.PumpkinPVPReloaded;
import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PumpkinExplodeEvent extends Event {

    protected @Nullable Player exploder;
    protected @NotNull Location explodeLocation;
    protected final @NotNull ExplosionTrigger.Action triggerAction;
    protected float explodePower;
    protected boolean setFire, breakBlocks;

    public PumpkinExplodeEvent(
            @NotNull ExplosionTrigger.Action triggerAction,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explodePower, boolean setFire, boolean breakBlocks
    ) {
        this.exploder = exploder;
        this.explodeLocation = explodeLocation;
        this.triggerAction = triggerAction;
        this.explodePower = explodePower;
        this.setFire = setFire;
        this.breakBlocks = breakBlocks;
    }

    public PumpkinExplodeEvent(ExplosionTrigger.Action triggerAction, @Nullable Player exploder, @NotNull Location explodeLocation) {
        this(
                triggerAction,
                exploder,
                explodeLocation,
                PumpkinPVPReloaded.config().explosion_power,
                PumpkinPVPReloaded.config().explosion_set_fire,
                PumpkinPVPReloaded.config().explosion_break_blocks
        );
    }

    public @NotNull ExplosionTrigger.Action getTriggerAction() {
        return triggerAction;
    }

    public @Nullable Player getExploder() {
        return exploder;
    }

    public @NotNull Location getLocation() {
        return explodeLocation;
    }

    public float getPower() {
        return explodePower;
    }

    public boolean getFire() {
        return setFire;
    }

    public boolean getBreakBlocks() {
        return breakBlocks;
    }
}
