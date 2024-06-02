package me.chimkenu.mangax.listeners;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class GameListener implements Listener {
    protected final JavaPlugin plugin;

    public GameListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }
}
