package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Stand;
import org.bukkit.util.Vector;

public class DashAction extends Action {
    private final Vector direction;

    /**
     * Represents a dash action
     *
     * @param goal the TargetGoal associated with this action
     * @param direction the direction to dash to
     * @param next the next action once this action is finished
     */
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
        }
    }
}
