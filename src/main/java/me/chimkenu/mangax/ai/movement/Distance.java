package me.chimkenu.mangax.ai.movement;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

public class Distance implements Movement {
    @Override
    public void run(Mob mob, Pathfinder pathfinder, LivingEntity target) {
        pathfinder.moveTo(mob.getLocation().add(target.getLocation().getDirection().multiply(3)));
    }
}
