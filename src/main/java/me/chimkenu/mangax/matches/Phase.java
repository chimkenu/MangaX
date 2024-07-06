package me.chimkenu.mangax.matches;

import org.bukkit.World;

import java.util.HashSet;

public interface Phase {
    default void start(World world, HashSet<Match.TeamPlayer> players) {}
    default void tick(World world, HashSet<Match.TeamPlayer> players) {}
    default void stop(World world, HashSet<Match.TeamPlayer> players) {}
}
