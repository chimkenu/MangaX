package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Kamehameha extends Move {
    public Kamehameha() {
        super(null, null, 40, 30 * 20, Material.HEART_OF_THE_SEA, Component.text("KAMEHAMEHA!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, player) -> {
            // Charge
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline() || player.getCooldown(getMaterial()) < getCooldown()) {
                        cancel();
                        return;
                    }
                    player.getWorld().spawnParticle(Particle.FLASH, player.getLocation(), 1, 0, 0, 0, 0);
                }
            }.runTaskTimer(plugin, 0, 1);
        };

        this.followUp = (plugin, player) -> {
            int chargeTime = getFollowUpTime() - (player.getCooldown(getMaterial()) - getCooldown());
            new BukkitRunnable() {
                int t = 20;
                final Location loc = player.getEyeLocation();
                @Override
                public void run() {
                    if (t <= 0 || player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    for (int i = 0; i <= 16; i++) {
                        float rotate = i * 34;
                        Location newLoc = loc.clone();
                        newLoc.setYaw(newLoc.getYaw() + (float) (3 * Math.cos(rotate)));
                        newLoc.setPitch(newLoc.getPitch() + (float) (3 * Math.sin(rotate)));
                        ParticleEffects.create(plugin, player.getWorld(), newLoc.toVector(), newLoc.getDirection(), 30, 0, new ParticleEffects.Effect() {
                            @Override
                            public void playParticle(World world, Location location, int index) {
                                world.spawnParticle(Particle.SOUL_FIRE_FLAME, location, 1, 0, 0, 0, 0.01);
                            }

                            @Override
                            public void intersect(LivingEntity livingEntity) {
                                if (!livingEntity.getType().equals(EntityType.ARMOR_STAND) && livingEntity != player) {
                                    livingEntity.damage(chargeTime / 4f, player);
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
