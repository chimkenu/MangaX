package me.chimkenu.mangax.events;

import me.chimkenu.mangax.enums.Moves;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class MoveTriggerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final LivingEntity entity;
    private final Moves move;
    private CancelReason cancelReason;
    private final boolean isFollowUp;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public MoveTriggerEvent(LivingEntity entity, Moves move, boolean isFollowUp) {
        this.entity = entity;
        this.move = move;
        this.isCancelled = false;
        this.cancelReason = null;
        this.isFollowUp = isFollowUp;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        if (cancel && getCancelReason() == null)
            cancelReason = CancelReason.OTHER;
        isCancelled = cancel;
    }

    public void cancel(CancelReason reason) {
        switch (reason) {
            case IN_COOLDOWN -> getEntity().sendActionBar(text("This move is on cooldown!", NamedTextColor.RED));
            case STUNNED -> getEntity().sendActionBar(text("You're currently stunned!", NamedTextColor.RED));
            case DISABLED -> getEntity().sendActionBar(text("You cannot use this move right now.", NamedTextColor.RED));
        }
        cancelReason = reason;
        setCancelled(true);
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public Moves getMove() {
        return move;
    }

    public CancelReason getCancelReason() {
        return cancelReason;
    }

    public boolean isFollowUp() {
        return isFollowUp;
    }

    public enum CancelReason {
        IN_COOLDOWN,
        STUNNED,
        DISABLED,
        OTHER
    }
}
