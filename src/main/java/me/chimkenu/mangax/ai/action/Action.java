package me.chimkenu.mangax.ai.action;

import me.chimkenu.mangax.ai.goals.TargetGoal;
import me.chimkenu.mangax.ai.movement.Movement;

public abstract class Action {
    protected final TargetGoal goal;
    private boolean hasStarted;
    private boolean hasStopped;
    public int time;
    public Movement movement;
    public Action next;

    /**
     * Represents an action that an entity can do.
     *
     * @param goal the TargetGoal associated with this action
     * @param time duration of action
     * @param movement movement type of entity while doing this action
     * @param next the next action once this action is finished
     */
    public Action(TargetGoal goal, int time, Movement movement, Action next) {
        this.goal = goal;
        hasStarted = false;
        hasStopped = false;
        this.time = time;
        this.movement = movement;
        this.next = next;
    }

    public abstract boolean preconditions();

    public void start() {
        hasStarted = true;
    }

    public void stop() {
        hasStopped = true;
    }

    public void tick() {
        if (!hasStarted) {
            start();
        }

        time--;
        if (time <= 0) {
            stop();
        }
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean hasStopped() {
        return hasStopped;
    }
}
