package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PumpkinBlockExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private @NotNull Block pumpkin;
    private final boolean hasExploded;

    public PumpkinBlockExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull Block pumpkin,
            @Nullable Player exploder,
            @NotNull Location explosionLocation,
            float explosionPower, boolean setFire, boolean breakBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explosionLocation, explosionPower, setFire, breakBlocks);
        this.pumpkin = pumpkin;
        this.hasExploded = hasExploded;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }

    public void setPumpkin(@NotNull Block pumpkin) {
        this.pumpkin = pumpkin;
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
