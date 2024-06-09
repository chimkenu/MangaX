package me.chimkenu.mangax.characters.tanjiro;

import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class WaterWheel extends Move {
    public WaterWheel() {
        super(null, null, 0, 5 * 20, Material.STRING, Component.text("Water Wheel").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, player) -> {

            // Create stand
            ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setInvisible(false);
            stand.setArms(true);

            stand.setBodyPose(newEulerAngle(0, 2.1612144, 0));
            stand.setLeftArmPose(newEulerAngle(20, 0, -10));
            stand.setRightArmPose(newEulerAngle(-45, 0, 90));
            stand.setRightLegPose(newEulerAngle(30, 0, 30));
            stand.setHeadPose(newEulerAngle(3.4143248,-6.903701,0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.tanjiro")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(1920785));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            stand.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            stand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

            ArmorStand blade = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            setUpArmorStand(blade);
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(9429746));
            chestplate.setItemMeta(meta);
            blade.getEquipment().setChestplate(chestplate);

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 37, 0, false, false, false));

            // Animation and damage
            new BukkitRunnable() {
                int t = 37;
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
                    if (t <= 0 || move != Moves.TANJIRO_WATER_WHEEL) {
                        player.removePotionEffect(PotionEffectType.INVISIBILITY);
                        cancel();
                        return;
                    }

                    runCommand("execute at " + stand.getUniqueId() + " run particle minecraft:splash ^.3 ^1.4 ^1 0.1 0.1 0.1 1 40");
                    runCommand("execute at " + stand.getUniqueId() + " run particle minecraft:bubble_pop ^.3 ^1.4 ^1 0.1 0.1 0.1 0.1 100");
                    runCommand("execute at " + stand.getUniqueId() + " run tp " + stand.getUniqueId() + " ^ ^ ^ ~30 ~");
                    runCommand("execute at " + player.getUniqueId() + " run tp " + stand.getUniqueId() + " ~ ~ ~");
                    runCommand("execute at "+ stand.getUniqueId() + " run tp " + blade.getUniqueId() + " ^1 ^-.2 ^1 ~-90 ~");

                    for (LivingEntity e : player.getLocation().getNearbyLivingEntities(2)) {
                        if (!e.getType().equals(EntityType.ARMOR_STAND) && e != player) {
                            Vector direction = e.getLocation().toVector().subtract(player.getLocation().toVector());
                            direction = direction.normalize();
                            Vector v = e.getVelocity().add(direction.multiply(0.1)).add(new Vector(0, 0.1, 0));

                            MoveTargetEvent event = new MoveTargetEvent(Moves.TANJIRO_STRIKING_TIDE, player, e, 2.5, v);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return;
                            }

                            e.damage(event.getDamage(), player);
                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
                        }
                    }

                    t--;
                }

                @Override
                public void cancel() {
                    super.cancel();
                    stand.remove();
                    blade.remove();
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
