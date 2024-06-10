package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Kamehameha extends Move {
    public Kamehameha() {
        super(null, null, 40, 30 * 20, Material.HEART_OF_THE_SEA, Component.text("KAMEHAMEHA!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {

            // Charge
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity instanceof Player player) {
                        if (!player.isOnline() || player.getCooldown(getMaterial()) < getCooldown()) {
                            cancel();
                            return;
                        }
                    }

                    if (entity.isDead()) {
                        cancel();
                        return;
                    }
                    entity.getWorld().spawnParticle(Particle.FLASH, entity.getLocation(), 1, 0, 0, 0, 0);
                }
            }.runTaskTimer(plugin, 0, 1);
        };

        this.followUp = (plugin, entity) -> {
            int chargeTime = 40;
            if (entity instanceof Player player) {
                chargeTime = getFollowUpTime() - (player.getCooldown(getMaterial()) - getCooldown());
            }

            int finalChargeTime = chargeTime;
            new BukkitRunnable() {
                int t = 20;
                final Location loc = entity.getEyeLocation();
                @Override
                public void run() {
                    if (entity instanceof Player player && !player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (t <= 0 || entity.isDead()) {
                        cancel();
                        return;
                    }

                    for (int i = 0; i <= 16; i++) {
                        float rotate = i * 34;
                        Location newLoc = loc.clone();
                        newLoc.setYaw(newLoc.getYaw() + (float) (3 * Math.cos(rotate)));
                        newLoc.setPitch(newLoc.getPitch() + (float) (3 * Math.sin(rotate)));
                        ParticleEffects.create(plugin, entity.getWorld(), newLoc.toVector(), newLoc.getDirection(), 30, 0, new ParticleEffects.Effect() {
                            @Override
                            public void playParticle(World world, Location location, int index) {
                                world.spawnParticle(Particle.SOUL_FIRE_FLAME, location, 1, 0, 0, 0, 0.01);
                            }

                            @Override
                            public void intersect(LivingEntity livingEntity) {
                                if (!livingEntity.getType().equals(EntityType.ARMOR_STAND) && livingEntity != entity) {
                                    double damage = finalChargeTime / 4f;

                                    MoveTargetEvent event = new MoveTargetEvent(Moves.GOKU_KAMEHAMEHA, entity, livingEntity, damage, new Vector());
                                    Bukkit.getPluginManager().callEvent(event);
                                    if (event.isCancelled()) {
                                        return;
                                    }

                                    livingEntity.damage(event.getDamage(), entity);
                                    livingEntity.setVelocity(livingEntity.getVelocity().add(event.getKnockback()));
                                }
                            }
                        }, Integer.MAX_VALUE);
                    }

                    t--;
                }
            }.runTaskTimer(plugin, 0, 3);
        };
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
