package me.chimkenu.mangax.games;

import me.chimkenu.mangax.games.phases.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class DuelsGameManager implements GameManager, Listener {
    private final JavaPlugin plugin;
    private final Lobby lobby;
    private final World world;
    private final HashSet<Listener> listeners;
    private final TeamPlayers players;

    private BukkitTask mainTask;
    private final Queue<Phase> phases;
    private Phase currentPhase;

    private int errors;

    /**
     * Creates a team duel (or 1v1 if sets only have one player each)
     *
     * @param plugin the plugin instance
     * @param world a world instance
     * @param teamOne first team
     * @param teamTwo second team
     * @throws IllegalArgumentException when at least one team is empty or when a player is present in both teams
     */
    public DuelsGameManager(JavaPlugin plugin, Lobby lobby, World world, Set<Player> teamOne, Set<Player> teamTwo) {
        if (teamOne.isEmpty() || teamTwo.isEmpty()) {
            throw new IllegalArgumentException("Teams cannot have size 0");
        }

        for (Player player : teamOne) {
            if (teamTwo.contains(player)) {
                throw new IllegalArgumentException("A player cannot be in both teams!");
            }
        }

        this.plugin = plugin;
        this.lobby = lobby;
        this.world = world;
        this.listeners = new HashSet<>();
        players = new TeamPlayers(teamOne, teamTwo);

        phases = new LinkedList<>();
        phases.add(new ViewMapPhase(plugin, world, players));

        CharacterBanPhase characterBanPhase = new CharacterBanPhase(plugin, world, players);
        phases.add(characterBanPhase);
        phases.add(new CharacterSelectionPhase(plugin, world, players, characterBanPhase));
        phases.add(new MatchPhase(plugin, world, players));
        //phases.add(new EndPhase(plugin, world, players));
        currentPhase = null;

        errors = 0;
    }

    @Override
    public void start() {
        addListener(this);

        players.getTeamOne().forEach(p -> lobby.addToTeam(p, true));
        players.getTeamTwo().forEach(p -> lobby.addToTeam(p, false));

        next();
        mainTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (errors > 10) {
                    stop(true);
                }
                try {
                    if (currentPhase != null && !currentPhase.tick()) {
                        next();
                    }
                } catch (Exception ignored) {
                    errors++;
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @Override
    public void stop(boolean isAbrupt) {
        if (!isRunning()) return;
        clearListeners();
        players.forEach(lobby::sendToLobby);
        mainTask.cancel();
        mainTask = null;
    }

    private void next() {
        // stop current one (if it exists)
        if (currentPhase != null)
            currentPhase.stop();
        if (currentPhase instanceof Listener listener)
            removeListener(listener);

        // get next and start it
        currentPhase = phases.poll();
        if (currentPhase == null) {
            stop(false); // this is reached when there are no more phases i.e. the game is done
            return;
        }
        if (currentPhase instanceof Listener listener)
            addListener(listener);
        currentPhase.start();
    }

    @Override
    public boolean isRunning() {
        return mainTask != null && !mainTask.isCancelled();
    }

    @Override
    public JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public World getWorld() {
        return world;
    }

    @Override
    public Set<Player> getPlayers() {
        return null;
    }

    @Override
    public void addListener(Listener listener) {
        GameManager.super.addListener(listener);
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        GameManager.super.removeListener(listener);
        listeners.remove(listener);
    }

    @Override
    public void clearListeners() {
        for (Listener listener : listeners) {
            HandlerList.unregisterAll(listener);
        }
        listeners.clear();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (players.containsPlayer(e.getPlayer())) {
            players.forEach(p -> p.sendMessage(e.getPlayer().displayName().color(NamedTextColor.RED).append(Component.text(" left, stopping game..."))));
            stop(true);
        }
    }
}
