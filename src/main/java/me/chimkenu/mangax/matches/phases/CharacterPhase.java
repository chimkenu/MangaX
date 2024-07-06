package me.chimkenu.mangax.matches.phases;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.WorldData;
import me.chimkenu.mangax.matches.Match.TeamPlayer;
import me.chimkenu.mangax.matches.Phase;
import me.chimkenu.mangax.worlddata.Position;
import me.chimkenu.mangax.worlddata.Region;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class CharacterPhase implements Phase, Listener {
    private HashSet<TeamPlayer> teamPlayers;
    private World world;
    private final ArrayList<Player> players;
    private final ArrayList<Characters> selectedCharacters;
    private final HashSet<Characters> bannedCharacters;

    private final int TIME_TO_DECIDE = 15 * 20;
    private final boolean canBanCharacters;
    private int maxBans;

    private BoundingBox region = null;
    private Location spectatorSpawn = null;

    private Player active;
    private Characters selected;
    private int time;

    public CharacterPhase(boolean canBanCharacters) {
        players = new ArrayList<>();
        selectedCharacters = new ArrayList<>();
        bannedCharacters = new HashSet<>();
        this.canBanCharacters = canBanCharacters;
    }

    @Override
    public void start(World world, HashSet<TeamPlayer> players) {
        WorldData data = WorldData.CHARACTER_SELECTION_LOCATION;
        Position position = (Position) data.data.retrieveFrom(world, data.getKey());
        if (position != null)
            spectatorSpawn = position.toLocation(world);

        data = WorldData.CHARACTER_SELECTION_REGION;
        Region r = (Region) data.data.retrieveFrom(world, data.getKey());
        if (r != null)
            region = r.toBoundingBox();

        teamPlayers = players;
        this.world = world;
        maxBans = canBanCharacters ? Math.min(2, Math.max(0, (players.size() / 2) - 1)) : 0;
        next();
    }

    @Override
    public void tick(World world, HashSet<TeamPlayer> players) {
        if (time < 0) {
            next();
        }

        players.forEach(p -> p.player().sendActionBar(active.displayName()
                .append(Component.text(" is picking a character... "))
                .append(Component.text((time - (time % 20)) / 20))));

        time--;
    }

    @Override
    public void stop(World world, HashSet<TeamPlayer> players) {

    }

    private void next() {
        if (active != null) {
            // Ban phase
            if (bannedCharacters.size() < maxBans) {
                players.forEach(p -> p.sendActionBar(active.displayName()
                        .append(Component.text(" has banned "))
                        .append(Component.text(selected.toString()))));
                bannedCharacters.add(selected);

            // Character selection phase
            } else if (selectedCharacters.size() < teamPlayers.size()) {


            // Start game
            } else {

            }
        }

        // Default selection to a non-banned character
        selected = Arrays.stream(Characters.values()).filter(c -> !bannedCharacters.contains(c)).toList().getFirst();

        time = TIME_TO_DECIDE;


    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        for (TeamPlayer player : teamPlayers) {
            Player p = e.getPlayer();
            if (player.player() == p) {
                if (p == active) {

                } else if (!region.contains(p.getLocation().toVector())) {
                    p.teleport(spectatorSpawn);
                }
                return;
            }
        }
    }

    @EventHandler
    public void onClick(PlayerArmSwingEvent e) {
        if (e.getPlayer() == active) {

        }
    }
}
