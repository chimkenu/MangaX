package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
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

import java.util.ArrayList;

public class TheWorld extends Move {
    public TheWorld() {
        super(null, null, 120, 260, Material.CLOCK, Component.text("ZA WARUDO").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, player) -> {
            player.setCooldown(getMaterial(), getCooldown());
            player.getWorld().spawnParticle(Particle.DUST, player.getEyeLocation(), 1500, 1, 1, 1, 0, new Particle.DustOptions(Color.YELLOW, 0.8f));

            final int CHARGE_TIME = 20;
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        this.cancel();
                        return;
                    }

                    player.setCooldown(getMaterial(), getCooldown() + getFollowUpTime() - CHARGE_TIME);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0, true, false, false));

                    Location loc = player.getLocation();
                    AreaEffectCloud areaEffectCloud = player.getWorld().spawn(loc, AreaEffectCloud.class);
                    areaEffectCloud.setColor(Color.BLACK);
                    areaEffectCloud.setDuration(getFollowUpTime() - CHARGE_TIME);
                    areaEffectCloud.setRadius(10);
                    areaEffectCloud.addScoreboardTag(player.getUniqueId() + ".cloud");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (areaEffectCloud.isDead() || player.isDead() || !player.isOnline()) {
                                followUp.activate(plugin, player);
                                this.cancel();
                                return;
                            }

                            for (LivingEntity e : loc.getNearbyLivingEntities(10)) {
                                if (!e.getType().equals(EntityType.ARMOR_STAND) && e != player && !e.hasPotionEffect(PotionEffectType.BLINDNESS)) {
                                    theWorld(plugin, areaEffectCloud, player, e, player.getCooldown(getMaterial()) - getCooldown());
                                }
                            }
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }.runTaskLater(plugin, CHARGE_TIME);
        };

        this.followUp = (plugin, player) -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kill @e[tag=" + player.getUniqueId() + ".cloud" + "]");
    }

    private void theWorld(JavaPlugin plugin, AreaEffectCloud cloud, Player player, LivingEntity target, int duration) {
        new BukkitRunnable() {
            int t = duration;
            final Location loc = target.getLocation();
            final double health = target.getHealth();
            @Override
            public void run() {
                if (target.isDead() || !player.isOnline()) {
                    this.cancel();
                    return;
                }

                // Eject based on damage
                if (t <= 0 || cloud.isDead() || player.isDead()) {
                    double diff = health - target.getHealth();
                    if (diff > 0) {
                        Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector());
                        direction = direction.normalize();
                        target.setVelocity(target.getVelocity().add(direction.multiply(diff * 0.5)).add(new Vector(0, 0.5, 0)));
                    }
                    this.cancel();
                    return;
                }

                target.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2, 0, false, false, false));
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 2, 4, false, false, false));
                target.teleport(loc);

                t--;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
