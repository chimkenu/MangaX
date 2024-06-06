package me.chimkenu.mangax.characters.tanjiro;

import me.chimkenu.mangax.utils.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
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

public class DropRippleThrust extends Move {
    public DropRippleThrust() {
        super((plugin, player) -> {

            // Create stand
            ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setInvisible(false);
            stand.setArms(true);

            stand.setBodyPose(newEulerAngle(0, -27, 0));
            stand.setLeftArmPose(newEulerAngle(-10, 0, -40));
            stand.setRightArmPose(newEulerAngle(-15, 90, 30));
            stand.setLeftLegPose(newEulerAngle(-1, 0, -61));
            stand.setRightLegPose(newEulerAngle(1, 0, -30));
            stand.setHeadPose(newEulerAngle(0, 60,0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.tanjiro")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(1920785));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            stand.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            stand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 0, false, false, false));
            player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(2)).add(new Vector(0, 0.5, 0)));

            new BukkitRunnable() {
                int t = 15;

                @Override
                public void run() {
                    if (t <= 0 || player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    runCommand("execute at " + stand.getUniqueId() + " run particle minecraft:splash ^-1.5 ^1.4 ^.2 0.1 0.1 0.1 1 40");
                    runCommand("execute at " + stand.getUniqueId() + " run particle minecraft:bubble_pop ^-1.5 ^1.4 ^.2 0.1 0.1 0.1 0.1 100");
                    runCommand("execute anchored eyes at " + player.getUniqueId() + " run tp " + stand.getUniqueId() + " ^ ^ ^ ~-90 ~");

                    Location loc = player.getLocation();
                    loc.add(0, player.getEyeHeight(), 0);
                    loc.setDirection(player.getVelocity().normalize());
                    loc.add(loc.getDirection().multiply(1.5));

                    for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                        if (e != player && !e.getType().equals(EntityType.ARMOR_STAND)) {
                            e.damage(4, player);
                            e.setNoDamageTicks(15);
                        }
                    }
                    t--;
                }

                @Override
                public void cancel() {
                    super.cancel();
                    stand.remove();
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 5 * 20, Material.PRISMARINE_SHARD, Component.text("Drop Ripple Thrust").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
