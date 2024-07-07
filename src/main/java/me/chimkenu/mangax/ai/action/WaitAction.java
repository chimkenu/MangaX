package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Movement;

public class WaitAction extends Action {

    /**
     * Represents a wait action / empty action
     *
     * @param goal the TargetGoal associated with this action
     * @param time the duration of the action
     * @param movement movement type of entity while doing this action
     * @param next the next action once this action is finished
     */
    public WaitAction(TargetGoal goal, int time, Movement movement, Action next) {
        super(goal, time, movement, next);
    }

    @Override
    public boolean preconditions() {
        return true;
    }
}
