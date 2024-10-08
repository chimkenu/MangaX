package me.chimkenu.mangax.characters.tanjiro;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class StrikingTide extends Move {
    public StrikingTide() {
        super(null, null, 0, 5 * 20, Material.BLUE_DYE, Component.text("Striking Tide").color(NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setInvisible(false);
            stand.setArms(true);

            stand.customName(entity.name());
            stand.setCustomNameVisible(true);

            stand.setBodyPose(newEulerAngle(0, -27, 0));
            stand.setLeftArmPose(newEulerAngle(-70, 36, 17));
            stand.setRightArmPose(newEulerAngle(-102, -30, 51));
            stand.setLeftLegPose(newEulerAngle(29, -60, -31));
            stand.setRightLegPose(newEulerAngle(-30, -30, 30));
            stand.setHeadPose(newEulerAngle(3,3.3,0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.tanjiro")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(1920785));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.NETHERITE_LEGGINGS));
            stand.getEquipment().setBoots(new ItemStack(Material.IRON_BOOTS));
            stand.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

            entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false));

            // Animation and damage
            new BukkitRunnable() {
                int t = 55;
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

                    if (t <= 0 || move != Moves.TANJIRO_STRIKING_TIDE) {
                        entity.removePotionEffect(PotionEffectType.INVISIBILITY);
                        cancel();
                        return;
                    }

                    stand.teleport(getRelativeLocation(entity.getLocation(), 0, 0, -0.4, 0, 0));
                    if (t % 5 == 0) {
                        runCommand("execute as " + stand.getUniqueId() + " run data merge entity @s {ShowArms:1b,NoBasePlate:1b,Pose:{Body:[0f,-30f,0f],LeftArm:[-88f,39f,-16f],RightArm:[-66f,-15f,-57f],LeftLeg:[-1f,-120f,-31f],RightLeg:[30f,30f,30f],Head:[3.0673966f,3.3364124f,0f]}}");
                    } else if ((t + 1) % 5 == 0) {
                        runCommand("execute as " + stand.getUniqueId() + " run data merge entity @s {ShowArms:1b,NoBasePlate:1b,Pose:{Body:[0f,-27f,0f],LeftArm:[-70f,36f,17f],RightArm:[-102f,-30f,51f],LeftLeg:[29f,-60f,-31f],RightLeg:[-30f,-30f,30f],Head:[3.0673966f,3.3364124f,0f]}}");

                        Location loc = entity.getEyeLocation();
                        loc = loc.add(loc.getDirection().multiply(2));
                        loc.getWorld().spawnParticle(Particle.SWEEP_ATTACK, loc, 10, 0.5, 0.5, 0.5, 0);
                        for (LivingEntity e : loc.getNearbyLivingEntities(2)) {
                            if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                                Vector direction = e.getLocation().toVector().subtract(entity.getLocation().toVector());
                                direction = direction.normalize();
                                Vector v = e.getVelocity().add(direction.multiply(0.1)).add(new Vector(0, 0.1, 0));

                                MoveTargetEvent event = new MoveTargetEvent(Moves.TANJIRO_STRIKING_TIDE, entity, e, 2.5, v);
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    continue;
                                }

                                e.damage(event.getDamage(), entity);
                                e.setVelocity(e.getVelocity().add(event.getKnockback()));
                            }
                        }
                    }

                    t--;
                }

                @Override
                public void cancel() {
                    super.cancel();
                    stand.remove();
                }
            }.runTaskTimer(plugin, 5, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.CLOSE, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 3, 1, 55, true);
    }
}
