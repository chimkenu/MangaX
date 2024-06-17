package me.chimkenu.mangax.characters.tanjiro;

import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
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

        this.activate = (plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
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

            ArmorStand blade = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(blade);
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(9429746));
            chestplate.setItemMeta(meta);
            blade.getEquipment().setChestplate(chestplate);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 37, 0, false, false, false));

            // Animation and damage
            new BukkitRunnable() {
                int t = 37;
                @Override
                public void run() {
                    if (entity instanceof Player player && !player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (entity.isDead()) {
                        cancel();
                        return;
                    }

                    EntityEquipment equipment = entity.getEquipment();
                    Moves move = null;
                    if (equipment != null) {
                        move = Moves.getMoveFromItem(equipment.getItemInMainHand());
                    }

                    if (t <= 0 || move != Moves.TANJIRO_WATER_WHEEL) {
                        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
                        cancel();
                        return;
                    }

                    stand.getWorld().spawnParticle(Particle.SPLASH, getRelativeLocation(stand.getLocation(), 0.3, 1.4, 1, 0, 0), 40, 0.1, 0.1, 0.1, 1);
                    stand.getWorld().spawnParticle(Particle.BUBBLE_POP, getRelativeLocation(stand.getLocation(), 0.3, 1.4, 1, 0, 0), 100, 0.1, 0.1, 0.1, 0.1);
                    Location loc = entity.getLocation();
                    loc.setYaw(stand.getYaw() + 30);
                    stand.teleport(loc);
                    blade.teleport(getRelativeLocation(stand.getLocation(), 1, -0.2, 1, -90, 0));

                    for (LivingEntity e : entity.getLocation().getNearbyLivingEntities(2)) {
                        if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                            Vector direction = e.getLocation().toVector().subtract(entity.getLocation().toVector());
                            direction = direction.normalize();
                            Vector v = e.getVelocity().add(direction.multiply(0.1)).add(new Vector(0, 0.1, 0));

                            MoveTargetEvent event = new MoveTargetEvent(Moves.TANJIRO_STRIKING_TIDE, entity, e, 2.5, v);
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return;
                            }

                            e.damage(event.getDamage(), entity);
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

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.CLOSE, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TYPICAL, 3, 3, 37, true);
    }
}
