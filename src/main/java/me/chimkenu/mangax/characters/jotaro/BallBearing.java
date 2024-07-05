package me.chimkenu.mangax.characters.jotaro;

import com.google.common.base.Strings;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class BallBearing extends Move implements Listener {
    private final String key = "JOTARO_BALL_BEARING";

    public BallBearing() {
        super(null, null, 5, 60, Material.FIREWORK_STAR, Component.text("Ball Bearing", NamedTextColor.GRAY).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            if (entity instanceof Player player) {
                int amount = player.getInventory().getItemInMainHand().getAmount();
                if (amount > 1) {
                    player.setCooldown(getMaterial(), 5);
                    player.getInventory().getItemInMainHand().setAmount(--amount);
                } else {
                    player.getInventory().getItemInMainHand().setAmount(3);
                }
            }

            Snowball ball = entity.launchProjectile(Snowball.class);
            ball.setItem(new ItemStack(Material.AIR));
            ball.addScoreboardTag(key);
            ball.addScoreboardTag(entity.getUniqueId().toString());
            ball.setGravity(false);
            entity.getWorld().playSound(entity, Sound.ITEM_TRIDENT_THROW, 1, 1.5f);

            BlockDisplay blockDisplay = entity.getWorld().spawn(ball.getLocation(), BlockDisplay.class);
            blockDisplay.setTeleportDuration(1);
            blockDisplay.setBlock(Material.POLISHED_ANDESITE.createBlockData());
            blockDisplay.setTransformation(new Transformation(new Vector3f(0.125f, 0.125f, 0.125f), new AxisAngle4f(), new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));

            ball.addScoreboardTag(blockDisplay.getUniqueId().toString());
            ball.setVelocity(entity.getEyeLocation().getDirection().multiply(2));

            new BukkitRunnable() {
                int t = 100;
                @Override
                public void run() {
                    if (t < 0 || entity.isDead() || ball.isDead()) {
                        ball.remove();
                        blockDisplay.remove();
                        cancel();
                        return;
                    }
                    blockDisplay.teleport(ball.getLocation());
                    ball.getWorld().spawnParticle(Particle.WHITE_ASH, ball.getLocation(), 5, 0.1, 0.1, 0.1, 0.05);
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.LONG, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 8, 1, 1, false);
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setAmount(3);
        return item;
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent e) {
        if (e.getEntity().getScoreboardTags().contains(key)) {
            e.getEntity().removeScoreboardTag(key);

            List<String> tags = e.getEntity().getScoreboardTags().stream().toList();
            LivingEntity source = findSource(tags);
            BlockDisplay blockDisplay = findBlockDisplay(tags);

            if (source != null && blockDisplay != null) {
                e.setCancelled(true);
                e.getEntity().remove();

                source.getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.25f, 0.5f);
                source.getWorld().spawnParticle(Particle.SMOKE, e.getEntity().getLocation(), 10, 0.1, 0.1, 0.1, 0.1);
                for (LivingEntity target : e.getEntity().getLocation().getNearbyLivingEntities(1.6)) {
                    if (!target.getType().equals(EntityType.ARMOR_STAND) && target != source) {
                        MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_BALL_BEARING, source, target, 2.5, new Vector());
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            continue;
                        }

                        target.damage(event.getDamage(), source);
                        target.setVelocity(target.getVelocity().add(event.getKnockback()));
                        target.setNoDamageTicks(0);
                    }
                }
            }
        }
    }

    private LivingEntity findSource(List<String> tags) {
        for (String tag : tags) {
            if (Bukkit.getEntity(UUID.fromString(tag)) instanceof LivingEntity source) {
                return source;
            }
        }
        return null;
    }

    private BlockDisplay findBlockDisplay(List<String> tags) {
        for (String tag : tags) {
            if (Bukkit.getEntity(UUID.fromString(tag)) instanceof BlockDisplay blockDisplay) {
                return blockDisplay;
            }
        }
        return null;
    }
}
