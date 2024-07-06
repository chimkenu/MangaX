package me.chimkenu.mangax.worlddata;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public record Position(double x, double y, double z, float yaw, float pitch) {
    public @NotNull Location toLocation(@NotNull World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
}
