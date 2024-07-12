package me.chimkenu.mangax.games;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public interface GameManager {

    void start();

    void stop(boolean isAbrupt);

    boolean isRunning();

    JavaPlugin getPlugin();

    World getWorld();

    TeamPlayers getPlayers();

    //PlayerStats getPlayerStat(Player player);

    default void addListener(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPlugin());
    }

    default void removeListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    void clearListeners();
}
