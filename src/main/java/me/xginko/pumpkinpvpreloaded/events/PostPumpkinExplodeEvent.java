package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PostPumpkinExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Player exploder;
    private final @NotNull ExplosionTrigger.TriggerAction triggerAction;
    private final boolean hasExploded;

    public PostPumpkinExplodeEvent(
            @NotNull Player exploder,
            @NotNull Location explosionLocation,
            float explosionPower,
            boolean setFire,
            boolean breakBlocks,
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            boolean hasExploded
    ) {
        super(explosionLocation, explosionPower, setFire, breakBlocks);
        this.hasExploded = hasExploded;
        this.exploder = exploder;
        this.triggerAction = triggerAction;
    }

    public @NotNull Player getExploder() {
        return exploder;
    }

    public @NotNull ExplosionTrigger.TriggerAction getTriggerAction() {
        return triggerAction;
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
