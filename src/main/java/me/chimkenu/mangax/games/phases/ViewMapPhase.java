package me.chimkenu.mangax.games.phases;

import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.games.Phase;
import me.chimkenu.mangax.games.TeamPlayers;
import me.chimkenu.mangax.worlddata.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.Duration;
import java.util.ArrayList;

import static net.kyori.adventure.text.Component.text;

public class ViewMapPhase implements Phase {
    private final JavaPlugin plugin;
    private final TeamPlayers players;
    private final ArrayList<Location> locations;

    private Component title;
    private Component subtitle;

    private int index = -1;
    private int time = 0;

    public ViewMapPhase(JavaPlugin plugin, World world, TeamPlayers players) {
        this.plugin = plugin;
        this.players = players;
        locations = new ArrayList<>();
        WorldData viewMapPos = WorldData.VIEW_MAP_POSITIONS;
        Position[] positions = (Position[]) viewMapPos.data.retrieveFrom(world, viewMapPos.getKey());
        if (positions == null) {
            return;
        }

        for (Position position : positions) {
            locations.add(position.toLocation(world));
        }

        WorldData mapName = WorldData.NAME;
        title = (Component) mapName.data.retrieveFrom(world, mapName.getKey());
        title = title == null ? text("") : title;
        WorldData builders = WorldData.BUILDERS;
        subtitle = (Component) builders.data.retrieveFrom(world, builders.getKey());
        subtitle = subtitle == null ? text("") : subtitle;
    }

    @Override
    public void start() {
        players.forEach(player -> {
            player.getInventory().clear();
            player.setGameMode(GameMode.SPECTATOR);
        });
        next();
        new BukkitRunnable() {
            @Override
            public void run() {
                players.forEach(player -> player.showTitle(Title.title(title, subtitle, Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(4000), Duration.ofMillis(500)))));
            }
        }.runTaskLater(plugin, 20);
    }

    @Override
    public boolean tick() {
        if (index < locations.size()) {
            players.forEach(player -> player.teleport(locations.get(index)));

            int TIME_PER_LOCATION = 40;
            if (time > TIME_PER_LOCATION) {
                next();
            }

            time++;
            return true;
        }
        return false;
    }

    private void next() {
        index++;
        time = 0;
    }
}
