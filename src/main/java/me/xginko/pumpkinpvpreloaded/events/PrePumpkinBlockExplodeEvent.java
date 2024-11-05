package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinBlockExplodeEvent extends PrePumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private @NotNull Block pumpkin;

    public PrePumpkinBlockExplodeEvent(
            @NotNull ExplosionTrigger.Action triggerAction,
            @NotNull Block pumpkin,
            @Nullable Player exploder,
            @NotNull Location explodeLocation
    ) {
        super(triggerAction, exploder, explodeLocation);
        this.pumpkin = pumpkin;
    }

    public @NotNull Block getPumpkin() {
        return pumpkin;
    }

    public void setPumpkin(@NotNull Block pumpkin) {
        this.pumpkin = pumpkin;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlers;
    }

    public static @NotNull HandlerList getHandlerList() {
        return handlers;
    }
}
