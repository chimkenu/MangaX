package me.chimkenu.mangax.characters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public interface Activate {
    /**
     * Runs when a move is triggered or used for the first time or used its follow-up
     *
     * @param plugin a MangaX plugin instance
     * @param entity the entity that triggered the move
     */
    void activate(JavaPlugin plugin, LivingEntity entity);
}
