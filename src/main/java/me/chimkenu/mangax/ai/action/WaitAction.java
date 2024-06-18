package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Movement;

public class WaitAction extends Action {
    public WaitAction(TargetGoal goal, int time, Movement movement, Action next) {
        super(goal, time, movement, next);
    }

    @Override
    public boolean preconditions() {
        return true;
    }
}
