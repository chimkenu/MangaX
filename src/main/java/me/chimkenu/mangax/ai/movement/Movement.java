package me.chimkenu.mangax.ai.movement;

import com.destroystokyo.paper.entity.Pathfinder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

public interface Movement {
    void run(Mob mob, Pathfinder pathfinder, LivingEntity target);
}
