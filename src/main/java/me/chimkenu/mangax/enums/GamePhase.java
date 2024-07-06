package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.matches.phases.*;
import me.chimkenu.mangax.matches.Phase;

public enum GamePhase {
    READY (new ReadyPhase()),
    BAN (new CharacterPhase(true)),
    MATCH (new MatchPhase()),
    END (new EndPhase());

    public final Phase phase;

    GamePhase(Phase phase) {
        this.phase = phase;
    }

    private static final GamePhase[] values = values();

    public GamePhase next() {
        return values[(this.ordinal() + 1) % values.length];
    }
}
