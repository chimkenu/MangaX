package me.chimkenu.mangax.matches;

import me.chimkenu.mangax.enums.GamePhase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;

public class Match {
    private final JavaPlugin plugin;
    private final World mainWorld;
    private final World gameWorld;

    private final HashSet<TeamPlayer> players;
    private final BukkitTask task;

    private GamePhase currentGamePhase;
    private Phase currentPhase;

    public Match(JavaPlugin plugin, World mainWorld, World gameWorld) {
        this.plugin = plugin;
        this.mainWorld = mainWorld;
        this.gameWorld = gameWorld;

        players = new HashSet<>();

        currentGamePhase = null;
        currentPhase = null;
        nextPhase();

        task = new BukkitRunnable() {
            @Override
            public void run() {
                if (currentPhase == null) {
                    cancel();
                    return;
                }
                currentPhase.tick(gameWorld, players);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    public void nextPhase() {
        if (currentGamePhase == null) {
            currentGamePhase = GamePhase.READY;
        } else {
            if (currentPhase instanceof Listener listener) {
                HandlerList.unregisterAll(listener);
            }

            currentGamePhase = currentGamePhase.next();
        }

        currentPhase = currentGamePhase.phase;
        currentPhase.start(gameWorld, players);
        if (currentPhase instanceof Listener listener) {
            Bukkit.getPluginManager().registerEvents(listener, plugin);
        }
    }

    public void stop(boolean isAbrupt) {
        task.cancel();
        for (TeamPlayer player : players) {
            player.player().sendMessage(Component.text("Stopping game...", NamedTextColor.YELLOW));
            player.player().teleport(mainWorld.getSpawnLocation());
        }
    }

    public record TeamPlayer(Player player, int team) {}
}

