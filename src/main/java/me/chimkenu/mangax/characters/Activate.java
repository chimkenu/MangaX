package me.chimkenu.mangax.characters;

import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;

public interface Activate {
    void activate(JavaPlugin plugin, LivingEntity entity);
}
