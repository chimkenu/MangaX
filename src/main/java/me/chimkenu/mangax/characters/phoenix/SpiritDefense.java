package me.chimkenu.mangax.characters.phoenix;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class SpiritDefense extends Move implements Listener {
    public SpiritDefense() {
        super(null, null, 100, 15 * 20, Material.AMETHYST_SHARD, Component.text("Spirit Defense").color(NamedTextColor.LIGHT_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Location loc = entity.getLocation();
            loc.setPitch(0);
            loc = getRelativeLocation(loc, 0, 0.1, 3, 0, 0);
            while (loc.getBlock().isPassable()) {
                loc.subtract(0, 0.01, 0);
            }

            ArmorStand maya = entity.getWorld().spawn(loc, ArmorStand.class);
            Objects.requireNonNull(maya.getAttribute(Attribute.GENERIC_SCALE)).setBaseValue(0.8);
            setUpArmorStand(maya);
            maya.setInvisible(false);
            maya.setHeadPose(newEulerAngle(349, 0, 0));
            maya.setLeftLegPose(newEulerAngle(11, 10, 0));
            maya.setRightLegPose(newEulerAngle(338, 0, 0));
            maya.setLeftArmPose(newEulerAngle(113, 139, 0));
            maya.setRightArmPose(newEulerAngle(113, 216, 0));

            maya.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.maya")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(0xc74edb));
            chestplate.setItemMeta(meta);
            maya.getEquipment().setChestplate(chestplate);
            maya.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
            maya.getEquipment().setBoots(boots);

            entity.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, getFollowUpTime(), 4, false, false, true));

            new BukkitRunnable() {
                final Location loc = getRelativeLocation(maya.getLocation(), 0, 0, -3, 0, 0);
                int t = getFollowUpTime();
                @Override
                public void run() {
                    if (t <= 0 || loc.distanceSquared(entity.getLocation()) > 4 * 4 || entity.hasPotionEffect(PotionEffectType.HUNGER)) {
                        maya.remove();
                        entity.removePotionEffect(PotionEffectType.RESISTANCE);
                        cancel();
                        return;
                    }

                    for (Entity e : loc.getNearbyEntities(2.5, 2.5, 2.5)) {
                        if (e.hasGravity() && e != entity) {
                            Vector direction = e.getLocation().toVector().subtract(loc.toVector());
                            direction = direction.normalize();
                            Vector v = (direction.multiply(1.5)).add(new Vector(0, 0.5, 0));

                            if (e instanceof LivingEntity living && !e.getType().equals(EntityType.ARMOR_STAND)) {
                                MoveTargetEvent event = new MoveTargetEvent(Moves.PHOENIX_SPIRIT_DEFENSE, entity, living, 0, v);
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    continue;
                                }

                                living.damage(event.getDamage(), entity);
                                e.setVelocity(event.getKnockback());
                                continue;
                            }
                            e.setVelocity(v);
                        }
                    }

                    for (int i = 0; i < 36; i++) {
                        loc.setYaw(loc.getYaw() + i * 10);
                        for (int j = 0; j < 20; j++) {
                            loc.setPitch(loc.getPitch() + j * 9 - 90);
                            loc.getWorld().spawnParticle(Particle.DUST, getRelativeLocation(loc, 0, 4, 0, 0, 0), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.fromRGB(232, 96, 209), 0.8f));
                        }
                    }
                    loc.setYaw(loc.getYaw() + 2);
                    loc.setPitch(loc.getPitch() + 2);

                    t--;
                }
            }.runTaskTimer(plugin, 1, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 2, 1, getFollowUpTime(), false);
    }
}
