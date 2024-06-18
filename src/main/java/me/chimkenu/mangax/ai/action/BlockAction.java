package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Distance;
import me.chimkenu.mangax.listeners.BlockListener;

public class BlockAction extends Action {
    public BlockAction(TargetGoal goal, int time, Action next) {
        super(goal, time, new Distance(), next);
    }

    @Override
    public boolean preconditions() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        BlockListener.toggleBlock(goal.mob, true);
    }

    @Override
    public void stop() {
        super.stop();
        BlockListener.toggleBlock(goal.mob, false);
    }
}
