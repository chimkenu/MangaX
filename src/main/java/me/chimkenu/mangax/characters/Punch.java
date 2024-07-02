package me.chimkenu.mangax.characters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public interface Punch {
    /**
     * Punch moves are moves that trigger on hitting an entity instead of simply left-clicking.
     * These moves will still go on cooldown even if the source misses
     *
     * @param plugin a plugin instance for BukkitRunnable
     * @param source the attacker
     * @param target the target
     * @param isFollowUp {@code true} if the attack was made during the move's follow-up time
     */
    void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp);
}
