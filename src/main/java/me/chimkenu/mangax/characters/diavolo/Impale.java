package me.chimkenu.mangax.characters.diavolo;

import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import me.chimkenu.mangax.characters.Move;
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

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class Impale extends Move {
    public Impale() {
        super((plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setLeftLegPose(newEulerAngle(0, 0, 351));
            stand.setRightLegPose(newEulerAngle(0, 0, 12));
            stand.setHeadPose(newEulerAngle(9, 0, 0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.king-crimson")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(16711680));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            meta = (LeatherArmorMeta) boots.getItemMeta();
            meta.setColor(Color.fromRGB(16711680));
            boots.setItemMeta(meta);
            stand.getEquipment().setBoots(boots);

            ArmorStand leftHand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            ArmorStand rightHand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            leftHand.setSmall(true);
            rightHand.setSmall(true);
            setUpArmorStand(leftHand);
            setUpArmorStand(rightHand);
            leftHand.getEquipment().setHelmet(new ItemStack(Material.IRON_BLOCK));
            rightHand.getEquipment().setHelmet(new ItemStack(Material.IRON_BLOCK));

            // Charge up
            new BukkitRunnable() {
                int t = 15;
                @Override
                public void run() {
                    if (entity.isDead()) {
                        clear();
                        cancel();
                        return;
                    }

                    // Actual attack
                    if (t <= 0) {
                        runCommand("execute at " + stand.getUniqueId() + " run tp " + rightHand.getUniqueId() + " ^-0.5 ^0.4 ^2 ~ ~");
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                clear();
                                for (LivingEntity e : rightHand.getLocation().getNearbyLivingEntities(1)) {
                                    damage(e);
                                    return;
                                }
                            }
                        }.runTaskLater(plugin, 2);
                        cancel();
                        return;
                    }

                    // "Charge" up attack
                    rightHand.getWorld().spawnParticle(Particle.CRIT, rightHand.getEyeLocation(), 1, 0.1, 0.1, 0.1, 0.1);
                    runCommand("execute anchored eyes at " + entity.getUniqueId() + " run tp " + stand.getUniqueId() + " ^0.7 ^0.2 ^0.5 ~ ~");
                    runCommand("execute at " + stand.getUniqueId() + " run tp " + rightHand.getUniqueId() + " ^-0.5 ^0.4 ^0.2 ~ ~");
                    runCommand("execute at " + stand.getUniqueId() + " run tp " + leftHand.getUniqueId() + " ^0.5 ^0.4 ^0.1 ~ ~");
                    t--;
                }

                private void clear() {
                    stand.remove();
                    leftHand.remove();
                    rightHand.remove();
                }

                private void damage(LivingEntity e) {
                    if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                        MoveTargetEvent event = new MoveTargetEvent(Moves.DIAVOLO_IMPALE, entity, e, 12, new Vector());
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }
                        e.damage(event.getDamage(), entity);
                        e.setVelocity(e.getVelocity().add(event.getKnockback()));

                        e.getWorld().playSound(e.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, 1, 0.5f);
                        e.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, e.getEyeLocation(), 6, 0.2, 0.2, 0.2, 0.4);
                        e.getWorld().spawnParticle(Particle.BLOCK, e.getEyeLocation(), 100, 0.2, 0.4, 0.2, 0.5, Material.REDSTONE_BLOCK.createBlockData());
                    }
                }
            }.runTaskTimer(plugin, 0, 1);

        }, null, 15, 15 * 20, Material.REDSTONE, Component.text("Impale").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.CLOSE, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TRICKY, 2, 15, 1, false);
    }
}
