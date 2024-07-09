package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ArmorStandUtil;
import me.chimkenu.mangax.utils.RandomUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
    private final String tag = "GOJO_BACKHAND";

    public Backhand() {
        super(null, null, 40, 100, Material.PAPER, Component.text("Backhand").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
        };

        this.followUp = (plugin, entity) -> {
            entity.removeScoreboardTag(tag);
        };
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.CLOSE, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.VERTICAL, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 2, 1, 40, true);
    }

    @Override
    public void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp) {
        if (!isFollowUp) {
            target.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, getFollowUpTime() + 10, 0, false, false, true));

            Location loc = source.getLocation();
            loc.setPitch(-70);
            Vector direction = loc.getDirection();
            direction.multiply(2);
            MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_BACKHAND, source, target, 2, direction);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.damage(event.getDamage(), source);
            target.setVelocity(event.getKnockback());
            source.addScoreboardTag(tag);

            final int delay = 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    target.setVelocity(new Vector());
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime() - delay, 200, false, false, true));
                    source.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, getFollowUpTime() - delay, 200, false, false, true));
                    source.teleport(ArmorStandUtil.getRelativeLocation(target.getEyeLocation(), 0, 0, -1.5, RandomUtil.randomFrom(-30, 30), RandomUtil.randomFrom(-30, 30)));
                }
            }.runTaskLater(plugin, delay);

        } else if (source.getScoreboardTags().contains(tag)) {
            MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_BACKHAND, source, target, 4, new Vector(0, -2, 0));
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.damage(event.getDamage(), source);
            target.setVelocity(event.getKnockback());

            target.removePotionEffect(PotionEffectType.SLOW_FALLING);
            source.removePotionEffect(PotionEffectType.SLOW_FALLING);
            target.removePotionEffect(PotionEffectType.HUNGER);
        }
    }
}
