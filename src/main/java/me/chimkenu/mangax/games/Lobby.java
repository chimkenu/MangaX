package me.chimkenu.mangax.games;

import me.chimkenu.mangax.utils.FileUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Lobby {
    private final JavaPlugin plugin;
    private final WorldManager worldManager;
    private final World lobbyWorld;
    private final HashMap<World, GameManager> games;

    private Team red;
    private Team blue;

    public Lobby(JavaPlugin plugin) {
        this.plugin = plugin;
        worldManager = new WorldManager(plugin.getConfig().getString("map-data.map-prefix", "mangax"), plugin.getConfig().getString("map-data.active-world-prefix", "game"), plugin.getConfig().getInt("map-data.worlds-per-map", 1));
        this.lobbyWorld = Bukkit.getWorld(plugin.getConfig().getString("map-data.lobby-world-name", "world"));
        this.games = new HashMap<>();

        // register teams
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        red = scoreboard.getTeam("red");
        if (red != null) red.unregister();
        blue = scoreboard.getTeam("blue");
        if (blue != null) blue.unregister();

        red = scoreboard.registerNewTeam("red");
        red.color(NamedTextColor.RED);
        red.setCanSeeFriendlyInvisibles(true);
        red.setAllowFriendlyFire(false);
        red.setCanSeeFriendlyInvisibles(true);
        blue = scoreboard.registerNewTeam("blue");
        blue.color(NamedTextColor.BLUE);
        blue.setCanSeeFriendlyInvisibles(true);
        blue.setCanSeeFriendlyInvisibles(true);
    }

    public boolean addDuelsGame(World world, Set<Player> teamOne, Set<Player> teamTwo) {
        if (getGames().get(world) != null) {
            return false; // cant start a game when world is currently occupied
        }

        DuelsGameManager duelsGameManager = new DuelsGameManager(plugin, this, world, teamOne, teamTwo);
        games.put(world, duelsGameManager);
        duelsGameManager.start();
        return true;
    }

    public boolean addDuelsGame(String worldName, Set<Player> teamOne, Set<Player> teamTwo) {
        for (World world : worldManager.worlds.keySet()) {
            // check if world name matches
            if (world.getName().equals(worldName)) {
                return addDuelsGame(world, teamOne, teamTwo);
            }
        }
        return false;
    }

    public boolean addDuelsGame(Set<Player> teamOne, Set<Player> teamTwo) {
        for (World world : worldManager.worlds.keySet()) {
            // check if world is available
            if (getGames().get(world) == null) {
                return addDuelsGame(world, teamOne, teamTwo);
            }
        }
        return false; // could not find an available world
    }

    public void stopAllGames() {
        games.forEach((world, game) -> game.stop(true));
        worldManager.unloadAll();
    }

    public HashMap<World, GameManager> getGames() {
        // clear worlds that are no longer running games
        Set<World> worldsToRemove = new HashSet<>();
        games.forEach((world, gameManager) -> {
            if (!gameManager.isRunning()) worldsToRemove.add(world);
        });
        worldsToRemove.forEach(games::remove);
        return games;
    }

    public void addToTeam(Player player, boolean isRed) {
        if (isRed) {
            red.addEntity(player);
        } else {
            blue.addEntities(player);
        }
    }

    public void sendToLobby(Player player) {
        red.removeEntity(player);
        blue.removeEntities(player);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(getLobbySpawn());
    }

    public Location getLobbySpawn() {
        return lobbyWorld.getSpawnLocation();
    }

    private static class WorldManager {
        HashMap<World, File> worlds;

        protected WorldManager(String mapPrefix, String activeWorldPrefix, int worldsPerMap) {
            worlds = new HashMap<>();
            File[] maps = Bukkit.getWorldContainer().listFiles((dir, name) -> name.contains(mapPrefix));
            if (maps == null || maps.length < 1) {
                return;
            }

            for (File map : maps) {
                for (int i = 0; i < worldsPerMap; i++) {
                    String name = activeWorldPrefix + "_" + map.getName() + "_" + i;
                    File container = new File(Bukkit.getWorldContainer(), name);
                    try {
                        FileUtils.copyDirectory(map.toPath().toString(), container.getPath());
                    } catch (IOException ignored) {}

                    WorldCreator creator = new WorldCreator(name);
                    creator.generator(new ChunkGenerator() {});
                    creator.generateStructures(false);
                    creator.type(WorldType.FLAT);
                    World world = Bukkit.createWorld(creator);
                    if (world != null) {
                        world.setAutoSave(false);
                        worlds.put(world, container);
                    }
                }
            }
        }

        protected void unloadAll() {
            worlds.forEach((world, file) -> {
                Bukkit.unloadWorld(world, false);
                try {
                    FileUtils.deleteDirectory(file.toPath());
                } catch (IOException ignored) {}
            });
        }
    }
}
