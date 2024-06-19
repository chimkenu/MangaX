package me.chimkenu.mangax.characters.naruto;

import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
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

                        clones.add(clone);
                    }
                }.runTaskLater(plugin, i * 5);
            }

            // Throw clones
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (ArmorStand stand : clones) {
                        stand.setVelocity(stand.getLocation().getDirection().multiply(8));
                    }
                }
            }.runTaskLater(plugin, 20);

            new BukkitRunnable() {
                final HashSet<LivingEntity> targets = new HashSet<>();
                int t = 10;
                @Override
                public void run() {
                    if (t <= 0) {
                        clones.forEach(Entity::remove);
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

                                MoveTargetEvent event = new MoveTargetEvent(Moves.NARUTO_MULTI_SHADOW_CLONE_JUTSU, entity, e, 8, new Vector());
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
                        stand.getWorld().spawnParticle(Particle.CLOUD, stand.getLocation(), 5, 0.2, 0, 0.2, 0.05);
                    }
                }
            }.runTaskTimer(plugin, 1, 1);

        }, null, 0, 20 * 20, Material.CREEPER_BANNER_PATTERN, Component.text("Multi Shadow Clone Jutsu").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
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
