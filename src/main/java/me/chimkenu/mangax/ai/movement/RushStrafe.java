package me.chimkenu.mangax.ai.movement;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.util.Vector;

import static me.chimkenu.mangax.utils.RandomUtil.probability;
import static me.chimkenu.mangax.utils.RandomUtil.sigmoid;

public class RushStrafe implements Movement {
    private final double switchTime;
    private final double random;
    private boolean toggle;
    private int timeSinceLastSwitch;

    public RushStrafe(double switchTime, double random) {
        this.switchTime = switchTime;
        this.random = random;
    }

    @Override
    public void run(Mob mob, Pathfinder pathfinder, LivingEntity target) {
        Vector direction = target.getLocation().toVector().subtract(mob.getLocation().toVector());
        Vector side = direction.getCrossProduct(new Vector(0, 1, 0));

        side = toggle ? side : side.multiply(-1);
        side.add(direction);

        pathfinder.moveTo(mob.getLocation().add(side));

        timeSinceLastSwitch++;
        if (probability(sigmoid(timeSinceLastSwitch, switchTime, random))) {
            toggle = !toggle;
            timeSinceLastSwitch = 0;
        }
    }
}
