package me.chimkenu.mangax.characters.jotaro;

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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class StandBarrage extends Move {
    public StandBarrage() {
        super(null, null, 0, 5 * 20, Material.NETHER_STAR, Component.text("Stand Barrage").color(NamedTextColor.YELLOW).decoration(TextDecoration.ITALIC, false).decorate(TextDecoration.BOLD));

        this.activate = (plugin, entity) -> {
            // Cancel if heavy hit is active
            if (entity instanceof Player player && player.getCooldown(Moves.JOTARO_HEAVY_HIT.move.getMaterial()) > Moves.JOTARO_HEAVY_HIT.move.getCooldown()) {
                player.setCooldown(getMaterial(), 1);
                return;
            }

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setLeftLegPose(newEulerAngle(0, 0, 351));
            stand.setRightLegPose(newEulerAngle(0, 0, 12));
            stand.setHeadPose(newEulerAngle(9, 0, 0));

            stand.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.star-platinum")));
            ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();
            meta.setColor(Color.fromRGB(11032002));
            chestplate.setItemMeta(meta);
            stand.getEquipment().setChestplate(chestplate);
            stand.getEquipment().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
            stand.getEquipment().setBoots(new ItemStack(Material.GOLDEN_BOOTS));

            ArmorStand leftHand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            ArmorStand rightHand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
            leftHand.setSmall(true);
            rightHand.setSmall(true);
            setUpArmorStand(leftHand);
            setUpArmorStand(rightHand);
            leftHand.getEquipment().setHelmet(new ItemStack(Material.GOLD_BLOCK));
            rightHand.getEquipment().setHelmet(new ItemStack(Material.GOLD_BLOCK));

            // Ora ora ora ora
            new BukkitRunnable() {
                int t = 30;

                @Override
                public void run() {
                    EntityEquipment equipment = entity.getEquipment();
                    Moves move = null;
                    if (equipment != null) {
                        move = Moves.getMoveFromItem(equipment.getItemInMainHand());
                    }

                    if (entity instanceof Player player && !player.isOnline()) {
                        cancel();
                        return;
                    }


                    if (t <= 0 || entity.isDead() || move == null || !move.equals(Moves.JOTARO_STAND_BARRAGE)) {
                        cancel();
                        return;
                    }

                    entity.getWorld().spawnParticle(Particle.CRIT, leftHand.getEyeLocation(), 5, 0.1, 0.1,0.1, 0);
                    entity.getWorld().spawnParticle(Particle.CRIT, rightHand.getEyeLocation(), 5, 0.1, 0.1,0.1, 0);
                    entity.getWorld().playSound(entity.getLocation(), Sound.BLOCK_PISTON_EXTEND, SoundCategory.PLAYERS, 1, 0.5f);
                    stand.teleport(getRelativeLocation(entity.getLocation(), 0, 0.5, 2, 0, 0));
                    leftHand.teleport(getRelativeLocation(stand.getLocation(), 0.5, 0.4, 0.5, 0, 0));
                    rightHand.teleport(getRelativeLocation(stand.getLocation(), -0.5, 0.4, 1, 0, 0));
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!entity.isDead()) {
                                leftHand.teleport(getRelativeLocation(stand.getLocation(), 0.5, 0.4, 1, 0, 0));
                                rightHand.teleport(getRelativeLocation(stand.getLocation(), -0.5, 0.4, 0.5, 0, 0));
                            }
                        }
                    }.runTaskLater(plugin, 1);

                    Location loc = entity.getEyeLocation();
                    for (int i = 0; i < 3; i++) {
                        loc.add(loc.getDirection());
                        for (LivingEntity e : loc.getNearbyLivingEntities(2)) {
                            if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                                MoveTargetEvent event = new MoveTargetEvent(Moves.JOTARO_STAND_BARRAGE, entity, e, 0.15, e.getVelocity().multiply(-0.9));
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return;
                                }

                                e.damage(event.getDamage(), entity);
                                e.setVelocity(e.getVelocity().add(event.getKnockback()));
                                e.setNoDamageTicks(0);
                            }
                        }
                    }

                    t--;
                }

                @Override
                public void cancel() {
                    super.cancel();
                    stand.remove();
                    leftHand.remove();
                    rightHand.remove();
                }
            }.runTaskTimer(plugin, 1, 2);
        };
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.CLOSE, MoveInfo.Knockback.NEGATIVE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL, 4, 2, 30, true);
    }
}
