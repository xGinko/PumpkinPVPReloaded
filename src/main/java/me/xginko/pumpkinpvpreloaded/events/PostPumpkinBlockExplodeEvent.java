package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PostPumpkinBlockExplodeEvent extends PostPumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull Block pumpkin;

    public PostPumpkinBlockExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull Block pumpkin,
            @Nullable Player exploder,
            @NotNull Location explosionLocation,
            float explosionPower, boolean setFire, boolean breakBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explosionLocation, explosionPower, setFire, breakBlocks, hasExploded);
        this.pumpkin = pumpkin;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
