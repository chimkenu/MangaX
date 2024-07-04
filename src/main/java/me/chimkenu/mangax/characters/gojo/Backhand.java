package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class Backhand extends Move implements Punch {
    public Backhand() {
        super(null, null, 15, 10, Material.PAPER, Component.text("Backhand"));

        this.activate = (plugin, entity) -> {

        };

        this.followUp = (plugin, entity) -> {

        };
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return null;
    }

    @Override
    public void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp) {
        if (!isFollowUp) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, getFollowUpTime(), 1, false, false, true));

            Location loc = source.getLocation();
            loc.setPitch(-70);
            Vector direction = loc.getDirection();
            direction.multiply(3);
            MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_BACKHAND, source, target, 2, direction);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.damage(event.getDamage());
            target.setVelocity(event.getKnockback());

            int delay = 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    target.setVelocity(new Vector());
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime() - delay, 200, false, false, true));
                    source.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime() - delay, 200, false, false, true));
                }
            }.runTaskLater(plugin, delay);

        } else {
            MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_BACKHAND, source, target, 8, new Vector(0, -2, 0));
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.damage(event.getDamage());
            target.setVelocity(event.getKnockback());

            target.removePotionEffect(PotionEffectType.SLOW_FALLING);
            source.removePotionEffect(PotionEffectType.SLOW_FALLING);
            target.removePotionEffect(PotionEffectType.HUNGER);
        }
    }
}
