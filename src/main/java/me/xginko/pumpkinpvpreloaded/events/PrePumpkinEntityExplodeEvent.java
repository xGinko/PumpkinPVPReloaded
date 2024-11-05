package me.xginko.pumpkinpvpreloaded.events;

import me.xginko.pumpkinpvpreloaded.modules.triggers.ExplosionTrigger;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinEntityExplodeEvent extends PrePumpkinExplodeEvent implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull LivingEntity livingEntity;

    public PrePumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull LivingEntity livingEntity,
            @Nullable Player exploder,
            @NotNull Location explodeLocation
    ) {
        super(triggerAction, exploder, explodeLocation);
        this.livingEntity = livingEntity;
        this.isCancelled = false;
    }

    public PrePumpkinEntityExplodeEvent(
            @NotNull ExplosionTrigger.TriggerAction triggerAction,
            @NotNull LivingEntity livingEntity,
            @Nullable Player exploder
    ) {
        this(triggerAction, livingEntity, exploder, livingEntity.getEyeLocation());
    }

    public @Nullable Player getExploder() {
        return livingEntity.getKiller();
    }

    public @NotNull LivingEntity getEntity() {
        return livingEntity;
    }

    public void setEntity(@NotNull LivingEntity livingEntity) {
        this.livingEntity = livingEntity;
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
