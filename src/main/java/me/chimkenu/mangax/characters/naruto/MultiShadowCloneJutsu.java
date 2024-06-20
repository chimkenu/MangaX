package me.chimkenu.mangax.characters.naruto;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class MultiShadowCloneJutsu extends Move {
    public MultiShadowCloneJutsu() {
        super((plugin, entity) -> {

            // Summon clones
            HashSet<ArmorStand> clones = new HashSet<>();
            for (int i = 0; i < 3; i++) {
                int finalI = i;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ArmorStand clone = entity.getWorld().spawn(getRelativeLocation(entity.getLocation(), -1 * (finalI + 1), 0, 0, 0, 0), ArmorStand.class);
                        setUpArmorStand(clone);
                        clone.setInvisible(false);
                        clone.setArms(true);
                        clone.setGravity(true);

                        clone.setBodyPose(newEulerAngle(15, 0, 0));
                        clone.setLeftArmPose(newEulerAngle(50, 30, -10));
                        clone.setRightArmPose(newEulerAngle(75, -30, 0));
                        clone.setLeftLegPose(newEulerAngle(-31, 0, -1));
                        clone.setRightLegPose(newEulerAngle(30, 0, 0));
                        clone.setHeadPose(newEulerAngle(0.9, -8.26, 0));

                        clone.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.naruto")));
                        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                        meta.setColor(Color.fromRGB(16746283));
                        chestplate.setItemMeta(meta);
                        clone.getEquipment().setChestplate(chestplate);
                        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                        meta = (LeatherArmorMeta) leggings.getItemMeta();
                        meta.setColor(Color.fromRGB(16746283));
                        leggings.setItemMeta(meta);
                        clone.getEquipment().setLeggings(leggings);
                        clone.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));

                        clone.getWorld().playSound(clone.getLocation(), Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 2);
                        clones.add(clone);
                    }
                }.runTaskLater(plugin, i * 5);
            }

            // Throw clones
            new BukkitRunnable() {
                int t = 3;
                @Override
                public void run() {
                    if (t <= 0) {
                        cancel();
                        return;
                    }
                    for (ArmorStand stand : clones) {
                        stand.setVelocity(stand.getLocation().getDirection().multiply(2.5).add(new Vector(0, 0.2, 0)));
                        if (t == 3) {
                            stand.getWorld().playSound(stand.getLocation(), Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5f, 0);
                        }
                    }
                    t--;
                }
            }.runTaskTimer(plugin, 20, 3);

            new BukkitRunnable() {
                final HashSet<LivingEntity> targets = new HashSet<>();
                int t = 10;
                @Override
                public void run() {
                    if (t <= 0) {
                        clones.forEach(e -> {
                            e.getWorld().playSound(e.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_CHARGE_ACTIVATE, SoundCategory.PLAYERS, 0.5f, 1.5f);
                            e.getWorld().spawnParticle(Particle.CLOUD, e.getEyeLocation(), 25, 0.2, 0.2, 0.2, 0.5);
                            e.remove();
                        });
                        clones.clear();
                        cancel();
                        return;
                    }

                    clones.forEach(clone -> {
                        for (LivingEntity e : clone.getLocation().getNearbyLivingEntities(3)) {
                            if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                                if (targets.contains(e)) {
                                    continue;
                                }
                                targets.add(e);

                                MoveTargetEvent event = new MoveTargetEvent(Moves.NARUTO_MULTI_SHADOW_CLONE_JUTSU, entity, e, 4, new Vector());
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    continue;
                                }
                                e.damage(event.getDamage(), entity);
                                e.setVelocity(e.getVelocity().add(event.getKnockback()));
                            }
                        }
                    });

                    t--;
                }
            }.runTaskTimer(plugin, 20, 1);


            // Particles
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (clones.isEmpty()) {
                        cancel();
                        return;
                    }

                    for (ArmorStand stand : clones) {
                        stand.getWorld().spawnParticle(Particle.CLOUD, stand.getLocation(), 2, 0.2, 0, 0.2, 0.05);
                    }
                }
            }.runTaskTimer(plugin, 1, 1);

        }, null, 0, 8 * 20, Material.CREEPER_BANNER_PATTERN, Component.text("Multi Shadow Clone Jutsu").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TYPICAL, 10, 1, 10, false);
    }
}
