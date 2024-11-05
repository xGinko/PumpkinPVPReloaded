package me.xginko.pumpkinpvpreloaded.events;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrePumpkinHeadEntityExplodeEvent extends PumpkinExplodeEvent implements Cancellable {

    private static final @NotNull HandlerList handlers = new HandlerList();
    private boolean isCancelled;

    private @NotNull LivingEntity pumpkinHead;

    public PrePumpkinHeadEntityExplodeEvent(@NotNull LivingEntity pumpkinHead, @NotNull Location explodeLocation) {
        super(explodeLocation);
        this.pumpkinHead = pumpkinHead;
        this.isCancelled = false;
    }

    public PrePumpkinHeadEntityExplodeEvent(@NotNull LivingEntity pumpkinHead) {
        this(pumpkinHead, pumpkinHead.getEyeLocation());
    }

    public @NotNull LivingEntity getPumpkinHeadEntity() {
        return pumpkinHead;
    }

    public void setPumpkinHeadEntity(@NotNull LivingEntity pumpkinHead) {
        this.pumpkinHead = pumpkinHead;
    }

    public @Nullable Player getKiller() {
        return pumpkinHead.getKiller();
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
