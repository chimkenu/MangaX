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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class DropRippleThrust extends Move {
    public DropRippleThrust() {
        super((plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
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

            entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 15, 0, false, false, false));
            Location loc = entity.getLocation();
            loc.setPitch(0);
            entity.setVelocity(entity.getVelocity().add(loc.getDirection().multiply(2)).add(new Vector(0, 0.5, 0)));

            new BukkitRunnable() {
                final HashSet<LivingEntity> targets = new HashSet<>();
                int t = 15;

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

                    stand.getWorld().spawnParticle(Particle.SPLASH, getRelativeLocation(stand.getLocation(), -1.5, 1.4, 0.2, 0, 0), 40, 0.1, 0.1, 0.1, 1);
                    stand.getWorld().spawnParticle(Particle.BUBBLE_POP, getRelativeLocation(stand.getLocation(), -1.5, 1.4, 0.2, 0, 0), 40, 0.1, 0.1, 0.1, 1);
                    stand.teleport(getRelativeLocation(entity.getLocation(), 0, 0, 0, -90, 0));

                    Location loc = entity.getLocation();
                    loc.add(0, entity.getEyeHeight(), 0);
                    loc.setDirection(entity.getVelocity().normalize());
                    loc.add(loc.getDirection().multiply(1.5));

                    for (LivingEntity e : loc.getNearbyLivingEntities(1.5)) {
                        if (e != entity && !e.getType().equals(EntityType.ARMOR_STAND)) {
                            if (targets.contains(e)) {
                                continue;
                            }
                            targets.add(e);

                            MoveTargetEvent event = new MoveTargetEvent(Moves.TANJIRO_DROP_RIPPLE_THRUST, entity, e, 6, new Vector());
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                continue;
                            }
                            e.damage(event.getDamage(), entity);
                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
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
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.FORWARD, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL,7, 1, 10, false);
    }
}
