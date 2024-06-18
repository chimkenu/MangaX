package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Stand;
import org.bukkit.util.Vector;

public class DashAction extends Action {
    private final Vector direction;

    public DashAction(TargetGoal goal, Vector direction, Action next) {
        super(goal, 5, new Stand(), next);
        this.direction = direction;
    }

    @Override
    public boolean preconditions() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        if (goal.attemptDash(direction, true)) {
            goal.mob.setVelocity(goal.mob.getVelocity().add(new Vector(0, 0.2, 0)));
        };
    }

}
