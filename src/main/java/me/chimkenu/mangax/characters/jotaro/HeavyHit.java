package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.chimkenu.mangax.ArmorStandUtil.*;

public class HeavyHit extends Move {
    public HeavyHit() {
        super((plugin, player) -> {

            // Create stand
            ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setLeftLegPose(new EulerAngle(0, 0, 351 * Math.PI / 180));
            stand.setRightLegPose(new EulerAngle(0, 0, 12 * Math.PI / 180));
            stand.setHeadPose(new EulerAngle(0, 0, 0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.star-platinum")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(11032002));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            stand.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));

            ArmorStand leftHand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            ArmorStand rightHand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            leftHand.setSmall(true);
            rightHand.setSmall(true);
            setUpArmorStand(leftHand);
            setUpArmorStand(rightHand);
            leftHand.getEquipment().setHelmet(new ItemStack(Material.GOLD_BLOCK));
            rightHand.getEquipment().setHelmet(new ItemStack(Material.GOLD_BLOCK));

            // Charge up
            new BukkitRunnable() {
                int t = 15;
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        clear();
                        cancel();
                        return;
                    }

                    // Actual attack
                    if (t <= 0) {
                        rightHand.getWorld().spawnParticle(Particle.FLASH, rightHand.getEyeLocation(), 1, 0, 0, 0, 0);
                        runCommand("execute at " + stand.getUniqueId() + " run tp " + rightHand.getUniqueId() + " ^-0.5 ^0.4 ^2 ~ ~");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                for (LivingEntity e : rightHand.getLocation().getNearbyLivingEntities(2)) {
                                    damage(e);
                                }
                                clear();
                            }
                        }.runTaskLater(plugin, 2);
                        cancel();
                        return;
                    }

                    // "Charge" up attack
                    rightHand.getWorld().spawnParticle(Particle.CRIT, rightHand.getEyeLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                    runCommand("execute anchored eyes at " + player.getUniqueId() + " run tp " + stand.getUniqueId() + " ^ ^0.5 ^2 ~ ~");
                    runCommand("execute at " + stand.getUniqueId() + " run tp " + rightHand.getUniqueId() + " ^-0.5 ^0.4 ^0.1 ~ ~");
                    runCommand("execute at " + stand.getUniqueId() + " run tp " + leftHand.getUniqueId() + " ^0.5 ^0.4 ^0.1 ~ ~");
                    t--;
                }

                private void clear() {
                    stand.remove();
                    leftHand.remove();
                    rightHand.remove();
                }

                private void damage(LivingEntity e) {
                    if (!e.getType().equals(EntityType.ARMOR_STAND) && e != player) {
                        e.damage(6, player);
                        Vector direction = e.getLocation().toVector().subtract(player.getLocation().toVector());
                        direction = direction.normalize();
                        e.setVelocity(e.getVelocity().add(direction.multiply(1.5)));
                        e.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, e.getEyeLocation(), 3, 0.2, 0.2, 0.2, 0.4);
                    }
                }
            }.runTaskTimer(plugin, 0, 1);

        }, null, 15, 10 * 20, Material.PURPLE_DYE, Component.text("Heavy Hit").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
