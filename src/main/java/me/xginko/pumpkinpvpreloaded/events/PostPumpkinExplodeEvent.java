package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class PostPumpkinExplodeEvent extends PumpkinExplodeEvent {

    private final boolean hasExploded;

    public PostPumpkinExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explodePower, boolean setFire, boolean breakBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explodeLocation, explodePower, setFire, breakBlocks);
        this.hasExploded = hasExploded;
    }

    public PostPumpkinExplodeEvent(ExplosionTrigger.TriggerAction triggerAction, @Nullable Player exploder, @NotNull Location explodeLocation, boolean hasExploded) {
        super(triggerAction, exploder, explodeLocation);
        this.hasExploded = hasExploded;
    }

    public boolean hasExploded() {
        return hasExploded;
    }
}
