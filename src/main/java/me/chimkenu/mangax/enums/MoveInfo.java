package me.chimkenu.mangax.enums;

public record MoveInfo(Damage damage, Range range, Knockback knockback, Manoeuvre manoeuvre, Type type, Difficulty difficulty, int recommendedRange, int chargeTime, int duration, boolean cancellable) {
    public enum Damage {
        NONE,
        LOW,
        MEDIUM,
        HIGH
    }

    public enum Range {
        SELF,
        CLOSE,
        MID,
        LONG
    }

    public enum Knockback {
        NONE,
        NEGATIVE,
        NORMAL,
        HIGH
    }

    public enum Manoeuvre {
        NONE,
        FORWARD,
        VERTICAL,
        OTHER
    }

    public enum Type {
        SINGLE,
        AREA,
        BUFF,
        DEBUFF,
        CONTROL,
        MANOEUVRE
    }

    public enum Difficulty {
        TRIVIAL,
        TYPICAL,
        TRICKY
    }
}
