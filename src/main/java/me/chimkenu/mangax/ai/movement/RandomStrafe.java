package me.chimkenu.mangax.ai.movement;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import static me.chimkenu.mangax.utils.RandomUtil.*;

public class RandomStrafe implements Movement {
    private final double radius;
    private final double switchTime;
    private final double random;
    private boolean toggle;
    private int timeSinceLastSwitch;

    public RandomStrafe(double radius, double switchTime, double random) {
        this.radius = radius;
        this.switchTime = switchTime;
        this.random = random;
    }

    @Override
    public void run(Mob mob, Pathfinder pathfinder, LivingEntity target) {
        Vector direction = target.getLocation().toVector().subtract(mob.getLocation().toVector());
        Vector side = direction.getCrossProduct(new Vector(0, 1, 0));

        side = toggle ? side : side.multiply(-1);

        double displacement = direction.lengthSquared();
        if (displacement > radius * radius) {
            side.add(direction);
        } else if (displacement < radius * radius) {
            side.subtract(direction);
        }

        pathfinder.moveTo(mob.getLocation().add(side));

        timeSinceLastSwitch++;
        if (probability(sigmoid(timeSinceLastSwitch, switchTime, random))) {
            toggle = !toggle;
            timeSinceLastSwitch = 0;
        }
    }
}
