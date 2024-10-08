package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ArmorStandUtil;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class Kamehameha extends Move {
    public Kamehameha() {
        super(null, null, 40, 20 * 20, Material.HEART_OF_THE_SEA, Component.text("KAMEHAMEHA!").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {

            // Charge
            new BukkitRunnable() {
                int t = getFollowUpTime();
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

                    // show action bar charge up bar
                    int charge = (getFollowUpTime() - t) / 4;
                    Component bar = text("[", NamedTextColor.AQUA);
                    for (int i = 0; i < getFollowUpTime() / 4; i++) {
                        bar = bar.append(text("|", i < charge ? NamedTextColor.AQUA : NamedTextColor.GRAY));
                    }
                    bar = bar.append(text("]", NamedTextColor.AQUA));
                    entity.sendActionBar(bar);
                    t--;

                    entity.getWorld().spawnParticle(Particle.FLASH, ArmorStandUtil.getRelativeLocation(entity.getLocation(), 0, 1, -1, 0, 0), 1, 0, 0, 0, 0);
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
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.LONG, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TRICKY, 20, 40, 20, true);
    }
}
