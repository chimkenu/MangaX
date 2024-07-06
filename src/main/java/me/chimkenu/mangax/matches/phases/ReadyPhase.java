
import io.papermc.paper.event.player.AsyncChatEvent;
import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.matches.Match.TeamPlayer;
import me.chimkenu.mangax.matches.Phase;
import me.chimkenu.mangax.worlddata.Position;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;

public class ReadyPhase implements Phase, Listener {
    private final HashSet<Player> readyPlayers = new HashSet<>();
    private HashSet<TeamPlayer> players;

    @Override
    public void start(World world, HashSet<TeamPlayer> players) {
        this.players = players;
        for (TeamPlayer player : players) {
            player.player().sendMessage(Component.text("A match has been found! Waiting for all players to type '", NamedTextColor.YELLOW)
                    .append(Component.text("Ready", NamedTextColor.GOLD))
                    .append(Component.text("'.", NamedTextColor.YELLOW)));
        }
    }

    @Override
    public void tick(World world, HashSet<TeamPlayer> players) {
        this.players = players;

        Component message = Component.text("");
        for (TeamPlayer player : players) {
            message = message.append(player.player().name().color(isReady(player) ? NamedTextColor.GREEN : NamedTextColor.GRAY));
        }

        for (TeamPlayer player : players) {
            player.player().sendActionBar(message.append(isReady(player) ? Component.text("") : Component.text("Type 'Ready' in chat!")));
        }
    }

    @Override
    public void stop(World world, HashSet<TeamPlayer> players) {
        WorldData data = WorldData.CHARACTER_SELECTION_LOCATION;
        Position position = (Position) data.data.retrieveFrom(world, data.getKey());
        if (position == null) {
            return;
        }
        Location loc = position.toLocation(world);
        players.forEach(teamPlayer -> {
            Player player = teamPlayer.player();
            player.teleport(loc);
            player.setGameMode(GameMode.SPECTATOR);
        });
    }

    private boolean isReady(TeamPlayer player) {
        return readyPlayers.contains(player.player());
    }

    @EventHandler
    public void onChat(AsyncChatEvent e) {
        for (TeamPlayer player : players) {
            if (player.player() == e.getPlayer() && e.message().examinableName().toLowerCase().contains("ready")) {
                readyPlayers.add(e.getPlayer());
            }
        }
    }
}
