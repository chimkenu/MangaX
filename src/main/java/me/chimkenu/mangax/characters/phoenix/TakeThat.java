package me.chimkenu.mangax.characters.phoenix;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.UUID;

public class TakeThat extends Move implements Listener {
    private final String key = "PHOENIX_TAKE_THAT";

    public TakeThat() {
        super(null, null, 0, 60, Material.PAPER, Component.text("Take That!").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Snowball that = entity.launchProjectile(Snowball.class);
            that.setItem(getItem());
            that.addScoreboardTag(key);
            that.addScoreboardTag(entity.getUniqueId().toString());
            entity.getWorld().playSound(entity, Sound.ITEM_BOOK_PAGE_TURN, 1, 2);
            that.setVelocity(entity.getEyeLocation().getDirection().multiply(2));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead() || that.isDead()) {
                        cancel();
                        return;
                    }
                    that.getWorld().spawnParticle(Particle.WHITE_SMOKE, that.getLocation(), 5, 0.1, 0.1, 0.1, 0.05);
                }
            }.runTaskTimer(plugin, 3, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.LONG, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 8, 1, 1, false);
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent e) {
        if (e.getEntity().getScoreboardTags().contains(key)) {
            e.getEntity().removeScoreboardTag(key);
            if (Bukkit.getEntity(UUID.fromString(e.getEntity().getScoreboardTags().iterator().next())) instanceof LivingEntity entity) {
                entity.getWorld().playSound(e.getEntity().getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 0.5f);
                entity.getWorld().spawnParticle(Particle.WHITE_SMOKE, e.getEntity().getLocation(), 50, 0.1, 0.1, 0.1, 0.25);
                for (LivingEntity target : e.getEntity().getLocation().getNearbyLivingEntities(2)) {
                    if (!target.getType().equals(EntityType.ARMOR_STAND) && target != entity) {
                        MoveTargetEvent event = new MoveTargetEvent(Moves.PHOENIX_TAKE_THAT, entity, target, 4, new Vector());
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            continue;
                        }

                        target.damage(event.getDamage(), entity);
                        target.setVelocity(target.getVelocity().add(event.getKnockback()));
                    }
                }
            }
        }
    }
}
