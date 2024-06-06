package me.chimkenu.mangax.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ParticleEffects {
    public static void create(JavaPlugin plugin, World world, Vector origin, Vector direction, double range, int duration, Effect effect, int maxWallsPassed) {
        RayTrace ray = new RayTrace(origin, direction);
        double accuracy = 0.4;

        if (duration > 0) {
            accuracy = range / duration;
            ArrayList<Vector> vectorArrayList = ray.traverse(range, accuracy);

            double finalAccuracy = accuracy;
            new BukkitRunnable() {
                int i = 0;
                int wallsPassed = 0;

                @Override
                public void run() {
                    Location loc = vectorArrayList.get(i).toLocation(world);
                    loc.setDirection(direction);
                    effect.playParticle(world, loc, i);

                    // Entity intersection
                    for (Entity e : world.getNearbyEntities(loc, finalAccuracy, finalAccuracy, finalAccuracy)) {
                        if (e instanceof LivingEntity living && !e.getType().equals(EntityType.ARMOR_STAND)) {
                            BoundingBox entityBox = living.getBoundingBox();
                            if (ray.intersects(entityBox, range, 0.1)) {
                                effect.intersect(living);
                            }
                        }
                    }

                    // Block intersection
                    Block block = world.getBlockAt(loc);
                    if (!block.isPassable() && ray.intersects(block.getBoundingBox(), range, 0.1)) {
                        wallsPassed++;

                        if (wallsPassed > maxWallsPassed)
                            this.cancel();
                    }

                    i++;
                    if (i >= vectorArrayList.size() - 1) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 1);

        } else {
            ArrayList<Vector> vectorArrayList = ray.traverse(range, accuracy);
            int wallsPassed = 0;
            int i = 0;
            for (Vector v : vectorArrayList) {
                Location loc = v.toLocation(world);
                loc.setDirection(direction);
                effect.playParticle(world, loc, i);
                i++;

                // Entity intersection
                for (Entity e : world.getNearbyEntities(loc, accuracy, accuracy, accuracy)) {
                    if (e instanceof LivingEntity living && !e.getType().equals(EntityType.ARMOR_STAND)) {
                        BoundingBox entityBox = living.getBoundingBox();
                        if (ray.intersects(entityBox, range, 0.1)) {
                            effect.intersect(living);
                        }
                    }
                }

                // Block intersection
                Block block = world.getBlockAt(loc);
                if (block.getType() != Material.AIR && ray.intersects(block.getBoundingBox(), range, 0.1)) {
                    wallsPassed++;

                    if (wallsPassed > maxWallsPassed)
                        return;
                }
            }
        }
    }

    public interface Effect {
        void playParticle(World world, Location location, int index);
        default void intersect(LivingEntity livingEntity) {}
    }
}
