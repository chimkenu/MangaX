package me.chimkenu.mangax.games;

public interface Phase {
    default void start() {}
    boolean tick();
    default void stop() {}
}
