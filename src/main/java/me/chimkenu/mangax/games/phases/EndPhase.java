package me.chimkenu.mangax.games.phases;

import me.chimkenu.mangax.games.Phase;

public class EndPhase implements Phase {
    @Override
    public void start() {
        Phase.super.start();
    }

    @Override
    public boolean tick() {
        return false;
    }

    @Override
    public void stop() {
        Phase.super.stop();
    }
}
