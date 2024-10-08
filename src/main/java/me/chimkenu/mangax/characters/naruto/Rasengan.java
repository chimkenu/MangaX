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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;
import static net.kyori.adventure.text.Component.text;

public class Rasengan extends Move {
    public Rasengan() {
        super(null, null, 40, 10 * 20, Material.HEART_OF_THE_SEA, Component.text("Rasengan").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setInvisible(false);
            stand.setArms(true);

            stand.setBodyPose(newEulerAngle(12, -0.1257, 0));
            stand.setLeftArmPose(newEulerAngle(-40, 0, 20));
            stand.setRightArmPose(newEulerAngle(-42, 0, -30));
            stand.setRightLegPose(newEulerAngle(30, 0, 0));
            stand.setHeadPose(newEulerAngle(4.0264,8.51,0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.naruto")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(16746283));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
            meta = (LeatherArmorMeta) leggings.getItemMeta();
            meta.setColor(Color.fromRGB(16746283));
            leggings.setItemMeta(meta);
            stand.getEquipment().setLeggings(leggings);
            stand.getEquipment().setBoots(new ItemStack(Material.NETHERITE_BOOTS));

            // Charge
            new BukkitRunnable() {
                int t = getFollowUpTime();
                @Override
                public void run() {
                    if (entity.isDead() || t < 0 || (entity instanceof Player player && (!player.isOnline() || player.getCooldown(getMaterial()) < getCooldown()))) {
                        stand.remove();
                        cancel();
                        return;
                    }

                    Location loc = entity.getLocation();
                    loc.setPitch(0);
                    stand.teleport(getRelativeLocation(loc, -0.9, 0, 0.2, -65, 0));
                    Location rel = getRelativeLocation(stand.getLocation(), 0, 0.9, 0.4, 0, 0);
                    stand.getWorld().spawnParticle(Particle.DUST, rel, 10, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.fromRGB(77, 204, 255), 1));
                    stand.getWorld().spawnParticle(Particle.BLOCK, rel, 10,0.1, 0, 0.1, 0.01, Material.LIGHT_BLUE_GLAZED_TERRACOTTA.createBlockData());
                    stand.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, rel, 10, 0.1, 0, 0.1, 0.01);

                    // show action bar charge up bar
                    int charge = (getFollowUpTime() - t) / 4;
                    Component bar = text("[", NamedTextColor.AQUA);
                    for (int i = 0; i < getFollowUpTime() / 4; i++) {
                        bar = bar.append(text("|", i < charge ? NamedTextColor.AQUA : NamedTextColor.GRAY));
                    }

                    loc = entity.getEyeLocation();
                    loc = loc.add(loc.getDirection());
                    LivingEntity target = getNearestLivingEntity(entity, loc);
                    bar = bar.append(text("]", NamedTextColor.AQUA))
                            .append(text(" Target: " + (target != null ? target.getName() : "none"), NamedTextColor.WHITE));
                    entity.sendActionBar(bar);

                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };

        this.followUp = (plugin, entity) -> {
            Location loc = entity.getEyeLocation();
            loc = loc.add(loc.getDirection());

            LivingEntity nearest = getNearestLivingEntity(entity, loc);

            if (nearest == null) {
                loc.getWorld().spawnParticle(Particle.CLOUD, loc, 20, 0.1, 0.1, 0.1, 0.25);
                return;
            }

            int chargeTime = entity instanceof Player player ? player.getCooldown(getMaterial()) - getCooldown() : getFollowUpTime();
            double damage = 4 + 8f * (1 - (double) chargeTime / getFollowUpTime());

            Vector direction = nearest.getLocation().toVector().subtract(entity.getLocation().toVector());
            direction = direction.normalize();
            Vector v = nearest.getVelocity().add(direction.multiply(1.5)).add(new Vector(0, 0.2, 0));

            MoveTargetEvent event = new MoveTargetEvent(Moves.NARUTO_RASENGAN, entity, nearest, damage, v);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            nearest.damage(event.getDamage(), entity);
            nearest.setVelocity(nearest.getVelocity().add(event.getKnockback()));

            nearest.getWorld().spawnParticle(Particle.FLASH, nearest.getEyeLocation(), 1, 0, 0, 0, 0);
            nearest.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, nearest.getEyeLocation(), 100, 0.2, 0.2, 0.2, 0.2);
        };
    }

    private @Nullable LivingEntity getNearestLivingEntity(LivingEntity entity, Location loc) {
        LivingEntity nearest = null;
        double minDist = -1;
        for (LivingEntity e : loc.getNearbyLivingEntities(2)) {
            if (e.getType().equals(EntityType.ARMOR_STAND) || e == entity) {
                continue;
            }
            if (nearest == null || entity.getLocation().distanceSquared(e.getLocation()) < minDist) {
                nearest = e;
            }
        }
        return nearest;
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.CLOSE, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TRICKY, 2, 40, 1, true);
    }
}
