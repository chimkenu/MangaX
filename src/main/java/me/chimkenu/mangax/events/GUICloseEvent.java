package me.chimkenu.mangax.events;

import me.chimkenu.mangax.gui.GUI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class GUICloseEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final Player player;
    private final GUI gui;

    public GUICloseEvent(Player player, GUI gui) {
        this.player = player;
        this.gui = gui;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

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

    public Player getPlayer() {
        return player;
    }

    public GUI getGui() {
        return gui;
    }
}
