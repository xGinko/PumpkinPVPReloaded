package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PumpkinEntityExplodeEvent extends PumpkinExplodeEvent {

    private static final @NotNull HandlerList handlers = new HandlerList();

    private final @NotNull LivingEntity livingEntity;
    private final boolean hasExploded;

    public PumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull LivingEntity livingEntity,
            @Nullable Player exploder,
            @NotNull Location explodeLocation,
            float explosionPower, boolean setFire, boolean destroyedBlocks, boolean hasExploded
    ) {
        super(triggerAction, exploder, explodeLocation, explosionPower, setFire, destroyedBlocks);
        this.livingEntity = livingEntity;
        this.hasExploded = hasExploded;
    }

    public @NotNull LivingEntity getEntity() {
        return livingEntity;
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
