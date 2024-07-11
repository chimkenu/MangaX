package me.chimkenu.mangax.games;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.function.Consumer;

public class TeamPlayers {
    Set<Player> teamOne;
    Set<Player> teamTwo;

    public TeamPlayers(Set<Player> teamOne, Set<Player> teamTwo) {
        for (Player player : teamOne) {
            if (teamTwo.contains(player)) {
                throw new IllegalArgumentException("A player cannot be present in both teams!");
            }
        }

        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
    }

    public Set<Player> getTeamOne() {
        return teamOne;
    }

    public Set<Player> getTeamTwo() {
        return teamTwo;
    }

    public void forEach(Consumer<? super Player> action) {
        teamOne.forEach(action);
        teamTwo.forEach(action);
    }

    public boolean areTeammates(Player p1, Player p2) {
        return (teamOne.contains(p1) && teamOne.contains(p2)) || (teamTwo.contains(p1) && teamTwo.contains(p2));
    }

    public boolean containsPlayer(Player player) {
        return teamOne.contains(player) || teamTwo.contains(player);
    }

    public int size() {
        return teamOne.size() + teamTwo.size();
    }
}
