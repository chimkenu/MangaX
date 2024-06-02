package me.chimkenu.mangax.listeners;

import org.bukkit.event.Listener;

public enum Listeners {
    Move (MoveListener.class);

    public final Class<GameListener> listener;

    <T extends GameListener> Listeners(Class<T> listener) {
        this.listener = (Class<GameListener>) listener;
    }
}
