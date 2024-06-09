package me.chimkenu.mangax.events;

import me.chimkenu.mangax.enums.Moves;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class MoveTargetEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final Moves move;
    private final LivingEntity source;
    private final LivingEntity target;
    private double damage;
    private Vector knockback;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public MoveTargetEvent(Moves move, LivingEntity source, LivingEntity target, double damage, Vector knockback) {
        this.move = move;
        this.source = source;
        this.target = target;
        this.isCancelled = false;
        this.damage = damage;
        this.knockback = knockback;
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
        isCancelled = cancel;
    }

    public Moves getMove() {
        return move;
    }

    public LivingEntity getSource() {
        return source;
    }

    public LivingEntity getTarget() {
        return target;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public Vector getKnockback() {
        return knockback;
    }

    public void setKnockback(Vector knockback) {
        this.knockback = knockback;
    }
}
