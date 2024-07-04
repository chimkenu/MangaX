package me.chimkenu.mangax.matches;

import me.chimkenu.mangax.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class MatchManager {
    private final JavaPlugin plugin;
    private final WorldManager worldManager;
    private final HashMap<World, Match> matches;

    public MatchManager(JavaPlugin plugin) {
        this.plugin = plugin;
        worldManager = new WorldManager(plugin.getConfig().getString("map-data.map-prefix", "mangax"), plugin.getConfig().getString("map-data.active-world-prefix", "game"), plugin.getConfig().getInt("map-data.worlds-per-map", 1));
        matches = new HashMap<>();
    }

    public void newMatch() {
    }

    public void stopAll() {
        matches.forEach((world, match) -> match.stop(true));
        worldManager.unloadAll();
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
