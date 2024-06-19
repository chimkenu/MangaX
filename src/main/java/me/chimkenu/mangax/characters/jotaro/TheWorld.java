package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class TheWorld extends Move {
    public TheWorld() {
        super(null, null, 130, 30 * 20, Material.CLOCK, Component.text("ZA WARUDO").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            if (entity instanceof Player player)
                player.setCooldown(getMaterial(), getCooldown());
            entity.getWorld().spawnParticle(Particle.DUST, entity.getEyeLocation(), 1500, 1, 1, 1, 0, new Particle.DustOptions(Color.YELLOW, 0.8f));

            final int CHARGE_TIME = 30;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead()) {
                        cancel();
                        return;
                    }

                    if (entity instanceof Player player)
                        player.setCooldown(getMaterial(), getCooldown() + getFollowUpTime() - CHARGE_TIME);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, false));

                    Location loc = entity.getLocation();
                    AreaEffectCloud areaEffectCloud = entity.getWorld().spawn(loc, AreaEffectCloud.class);
                    areaEffectCloud.setColor(Color.BLACK);
                    areaEffectCloud.setDuration(getFollowUpTime() - CHARGE_TIME);
                    areaEffectCloud.setRadius(10);
                    areaEffectCloud.addScoreboardTag(entity.getUniqueId() + ".cloud");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (entity instanceof Player player && !player.isOnline()) {
                                cancel();
                                return;
                            }

                            if (areaEffectCloud.isDead() || entity.isDead()) {
                                followUp.activate(plugin, entity);
                                cancel();
                                return;
                            }

                            for (LivingEntity e : loc.getNearbyLivingEntities(10)) {
                                if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity && !e.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                                    theWorld(plugin, areaEffectCloud, entity, e, areaEffectCloud.getDuration() + 20);
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }.runTaskLater(plugin, CHARGE_TIME);
        };

        this.followUp = (plugin, entity) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[tag=" + entity.getUniqueId() + ".cloud" + "]");
    }

    private void theWorld(JavaPlugin plugin, AreaEffectCloud cloud, LivingEntity entity, LivingEntity target, int duration) {
        new BukkitRunnable() {
            int t = duration;
            final Location loc = target.getLocation();
            final double health = target.getHealth();
            @Override
            public void run() {
                if (target.isDead() || (entity instanceof Player player && !player.isOnline())) {
                    cancel();
                    return;
                }

                // Eject based on damage
                if (t <= 0 || cloud.isDead() || entity.isDead()) {
                    double diff = health - target.getHealth();
                    if (diff > 0) {
                        Vector direction = target.getLocation().toVector().subtract(entity.getLocation().toVector());
                        direction = direction.normalize();
                        Vector v = target.getVelocity().add(direction.multiply(diff * 0.5)).add(new Vector(0, 0.5, 0));

                        MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_ZA_WARUDO, entity, target, 0, v);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        target.setVelocity(target.getVelocity().add(event.getKnockback()));
                    }
                    cancel();
                    return;
                }

                MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_ZA_WARUDO, entity, target, 0, new Vector());
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    cancel();
                    return;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 4, false, false, false));
                target.teleport(loc);

                t--;
            }

            @Override
            public void cancel() {
                super.cancel();
                target.removePotionEffect(PotionEffectType.BLINDNESS);
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.MID, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.DEBUFF, MoveInfo.Difficulty.TYPICAL, 6, 20, 100, false);
    }
}
