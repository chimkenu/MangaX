package me.chimkenu.mangax.ai.movement;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

public class CircleStrafe implements Movement {
    private final double radius;

    public CircleStrafe(double radius) {
        this.radius = radius;
    }

    @Override
    public void run(Mob mob, Pathfinder pathfinder, LivingEntity target) {
        Vector direction = target.getLocation().toVector().subtract(mob.getLocation().toVector());
        Vector side = direction.getCrossProduct(new Vector(0, 1, 0));

        double displacement = direction.lengthSquared();
        if (displacement > radius * radius) {
            side.add(direction);
        } else if (displacement < radius * radius) {
            side.subtract(direction);
        }

        pathfinder.moveTo(mob.getLocation().add(side));
    }
}
