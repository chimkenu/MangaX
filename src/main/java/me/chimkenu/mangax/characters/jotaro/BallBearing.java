package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class BallBearing extends Move implements Listener {
    private final String key = "JOTARO_BALL_BEARING";

    private HashMap<LivingEntity, Integer> standAliveTime = new HashMap<>();

    public BallBearing() {
        super(null, null, 2, 60, Material.FIREWORK_STAR, Component.text("Ball Bearing", NamedTextColor.GRAY).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            if (entity instanceof Player player) {
                int amount = player.getInventory().getItemInMainHand().getAmount();
                if (amount > 1) {
                    player.setCooldown(getMaterial(), 2);
                    player.getInventory().getItemInMainHand().setAmount(--amount);
                } else {
                    player.getInventory().getItemInMainHand().setAmount(3);
                }
            }

            ArmorStand stand = null;
            if (!standAliveTime.containsKey(entity)) {
                Location loc = entity.getLocation();
                loc.setPitch(0);
                loc = getRelativeLocation(loc, -0.8, 0, -0.5, -40, 0);
                stand = entity.getWorld().spawn(loc, ArmorStand.class);
                setUpArmorStand(stand);
                stand.setInvisible(false);
                stand.setLeftLegPose(newEulerAngle(-7, 0, -10));
                stand.setLeftArmPose(newEulerAngle(-64, 48, -19));
                stand.setRightLegPose(newEulerAngle(9, 0, 13));
                stand.setRightArmPose(newEulerAngle(-54, -30, 4));
                stand.setHeadPose(newEulerAngle(9, 27, 0));
                stand.setBodyPose(newEulerAngle(3, -9, 0));

                stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.star-platinum")));
                ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
                meta.setColor(Color.fromRGB(11032002));
                ArmorMeta armorMeta = (ArmorMeta) meta;
                armorMeta.setTrim(new ArmorTrim(TrimMaterial.GOLD, TrimPattern.DUNE));
                chestplate.setItemMeta(armorMeta);
                stand.getEquipment().setChestplate(chestplate);
                ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                armorMeta = (ArmorMeta) leggings.getItemMeta();
                armorMeta.setTrim(new ArmorTrim(TrimMaterial.GOLD, TrimPattern.DUNE));
                leggings.setItemMeta(armorMeta);
                stand.getEquipment().setLeggings(leggings);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                armorMeta = (ArmorMeta) boots.getItemMeta();
                armorMeta.setTrim(new ArmorTrim(TrimMaterial.GOLD, TrimPattern.SNOUT));
                meta = (LeatherArmorMeta) armorMeta;
                meta.setColor(Color.fromRGB(0x30371f));
                boots.setItemMeta(meta);
                stand.getEquipment().setBoots(boots);
            }

            Snowball ball = entity.launchProjectile(Snowball.class);
            ball.setItem(new ItemStack(Material.AIR));
            ball.addScoreboardTag(key);
            ball.addScoreboardTag(entity.getUniqueId().toString());
            ball.setGravity(false);
            entity.getWorld().playSound(entity, Sound.ITEM_TRIDENT_THROW, 1, 1.5f);

            BlockDisplay blockDisplay = entity.getWorld().spawn(ball.getLocation(), BlockDisplay.class);
            blockDisplay.setTeleportDuration(1);
            blockDisplay.setBlock(Material.POLISHED_ANDESITE.createBlockData());
            blockDisplay.setTransformation(new Transformation(new Vector3f(-0.125f, -0.125f, -0.125f), new AxisAngle4f(), new Vector3f(0.25f, 0.25f, 0.25f), new AxisAngle4f()));

            ball.addScoreboardTag(blockDisplay.getUniqueId().toString());
            ball.setVelocity(entity.getEyeLocation().getDirection().multiply(2));

            standAliveTime.putIfAbsent(entity, 6);
            standAliveTime.put(entity, standAliveTime.get(entity) + 6);

            ArmorStand finalStand = stand;
            new BukkitRunnable() {
                int t = 100;
                @Override
                public void run() {
                    if (t < 0 || entity.isDead() || ball.isDead()) {
                        if (finalStand != null) finalStand.remove();
                        ball.remove();
                        blockDisplay.remove();
                        cancel();
                        return;
                    }

                    if (finalStand != null && !finalStand.isDead() && standAliveTime.containsKey(entity)) {
                        Location loc = entity.getLocation();
                        loc.setPitch(0);
                        finalStand.teleport(getRelativeLocation(loc, -0.8, 0, -0.5, -40, 0));

                        int time = standAliveTime.get(entity);
                        if (time <= 0) {
                            finalStand.remove();
                            standAliveTime.remove(entity);
                        } else {
                            standAliveTime.put(entity, time - 1);
                        }
                    }

                    blockDisplay.teleport(ball.getLocation());
                    ball.getWorld().spawnParticle(Particle.WHITE_ASH, ball.getLocation(), 5, 0.1, 0.1, 0.1, 0.05);
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {
            "<#c4c4c4>Uses Star Platinum to launch</#c4c4c4>",
            "<#c4c4c4>three (3) small ball bearings</#c4c4c4>",
            "<#c4c4c4>at opponents at high velocity,</#c4c4c4>",
            "<#c4c4c4>allowing the player to attack</#c4c4c4>",
            "<#c4c4c4>from longer distances.</#c4c4c4>",
            "<reset>",
            "<i:false><#ffffff>Type:</#ffffff> <u>Attack</u>",
            "<i:false><#ffffff>Range:</#ffffff> <#40eb8d>Very Long</#40eb8d>",
            "<i:false><#ffffff>Damage:</#ffffff> <#f54949><b>1</b> health",
            "<i:false><#ffffff>Cooldown:</#ffffff> <#e6dd6c><b>3</b> seconds</#e6dd6c>",
            "<reset>",
            "<i:false><#f6ff52><b>[<key:key.attack>]</b></#f6ff52> <#ffffff>to use ability</#ffffff>"
        };
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.LONG, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 8, 1, 1, false);
    }

    @Override
    public @NotNull ItemStack getItem() {
        ItemStack item = super.getItem();
        item.setAmount(3);
        return item;
    }

    @EventHandler
    public void onProjectileLand(ProjectileHitEvent e) {
        if (e.getEntity().getScoreboardTags().contains(key)) {
            e.getEntity().removeScoreboardTag(key);

            List<String> tags = e.getEntity().getScoreboardTags().stream().toList();
            LivingEntity source = findSource(tags);
            BlockDisplay blockDisplay = findBlockDisplay(tags);

            if (source != null && blockDisplay != null) {
                e.setCancelled(true);
                e.getEntity().remove();

                source.getWorld().playSound(e.getEntity().getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.25f, 0.5f);
                source.getWorld().spawnParticle(Particle.BLOCK, e.getEntity().getLocation(), 10, 0.1, 0.1, 0.1, 0.1, Material.POLISHED_ANDESITE.createBlockData());

                Entity hit = e.getHitEntity();
                LivingEntity target = null;
                if (!(hit instanceof LivingEntity) || hit.getType().equals(EntityType.ARMOR_STAND)) {
                    for (LivingEntity entity : e.getEntity().getLocation().getNearbyLivingEntities(1)) {
                        if (entity != source && !entity.getType().equals(EntityType.ARMOR_STAND)) {
                            target = entity;
                            break;
                        }
                    }
                } else {
                    target = (LivingEntity) hit;
                }

                if (target == null) return;

                if (!target.getType().equals(EntityType.ARMOR_STAND) && target != source) {
                    MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_BALL_BEARING, source, target, 2.5, new Vector());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    target.damage(event.getDamage(), source);
                    target.setVelocity(target.getVelocity().add(event.getKnockback()));
                    target.setNoDamageTicks(0);
                }
            }
        }
    }

    private LivingEntity findSource(List<String> tags) {
        for (String tag : tags) {
            if (Bukkit.getEntity(UUID.fromString(tag)) instanceof LivingEntity source) {
                return source;
            }
        }
        return null;
    }

    private BlockDisplay findBlockDisplay(List<String> tags) {
        for (String tag : tags) {
            if (Bukkit.getEntity(UUID.fromString(tag)) instanceof BlockDisplay blockDisplay) {
                return blockDisplay;
            }
        }
        return null;
    }
}
