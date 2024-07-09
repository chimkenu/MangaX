package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import static net.kyori.adventure.text.Component.text;
import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class NahIdWin extends Move {
    public NahIdWin() {
        super(null, null, 0, 40, Material.IRON_LEGGINGS, Component.text("NAH, I'D WIN.").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Bukkit.broadcast(entity.name()
                    .append(text(": "))
                    .append(getName()));

            new BukkitRunnable() {
                @Override
                public void run() {
                    Location loc = entity.getLocation();
                    entity.damage(entity.getHealth(), entity);
                    loc.setPitch(0);

                    while (loc.getBlock().isEmpty()) {
                        loc.add(0, -0.01, 0);
                    }
                    loc.add(0, 0.01, 0);

                    ArmorStand legs = loc.getWorld().spawn(loc, ArmorStand.class);
                    setUpArmorStand(legs);

                    legs.setLeftLegPose(newEulerAngle(-1, 0, -10));
                    legs.setRightLegPose(newEulerAngle(1, 0, 12));

                    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS);
                    ArmorMeta meta = (ArmorMeta) leggings.getItemMeta();
                    meta.setTrim(new ArmorTrim(TrimMaterial.NETHERITE, TrimPattern.RAISER));
                    leggings.setItemMeta(meta);
                    legs.getEquipment().setLeggings(leggings);
                    ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
                    LeatherArmorMeta leatherMeta = (LeatherArmorMeta) boots.getItemMeta();
                    leatherMeta.setColor(Color.fromRGB(0x1d1d21));
                    boots.setItemMeta(leatherMeta);
                    legs.getEquipment().setBoots(boots);

                    Location bodyLoc = getRelativeLocation(loc, 0, 0, -1.45, 0, 0);
                    if (bodyLoc.getBlock().isEmpty()) {
                        while (bodyLoc.getBlock().isEmpty()) {
                            bodyLoc.add(0, -0.01, 0);
                        }
                    } else {
                        while (!bodyLoc.getBlock().isEmpty()) {
                            bodyLoc.add(0, 0.01, 0);
                        }
                    }
                    bodyLoc.add(0, -1.1, 0);


                    ArmorStand body = bodyLoc.getWorld().spawn(bodyLoc, ArmorStand.class);
                    setUpArmorStand(body);

                    body.setLeftLegPose(newEulerAngle(-124, 0, -1));
                    body.setLeftArmPose(newEulerAngle(-79, -30, 10));
                    body.setRightLegPose(newEulerAngle(-78, 30, 10));
                    body.setRightArmPose(newEulerAngle(-78, 30, 10));
                    body.setHeadPose(newEulerAngle(-90, 0, 0));
                    body.setBodyPose(newEulerAngle(-72, 0, 0));

                    ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
                    leatherMeta = (LeatherArmorMeta) chestplate.getItemMeta();
                    leatherMeta.setColor(Color.fromRGB(0x1d1d21));
                    chestplate.setItemMeta(leatherMeta);
                    body.getEquipment().setChestplate(chestplate);
                    body.getEquipment().setHelmet(SkullUtil.getSkull(plugin.getConfig().getString("character-skins.gojo-winner")));

                    BlockDisplay blood = loc.getWorld().spawn(getRelativeLocation(loc, 0.275, 0.8,-0.15, 90, 0), BlockDisplay.class);
                    blood.setInvulnerable(true);
                    blood.setBlock(Material.REDSTONE_BLOCK.createBlockData());
                    blood.setTransformation(new Transformation(new Vector3f(), new AxisAngle4f(), new Vector3f(0.3f, 0.2f, 0.55f), new AxisAngle4f()));

                    new BukkitRunnable() {
                        int t = 100;
                        @Override
                        public void run() {
                            if (t < 0) {
                                body.remove();
                                legs.remove();
                                blood.remove();
                                cancel();
                                return;
                            }

                            body.getWorld().spawnParticle(Particle.BLOCK, getRelativeLocation(body.getLocation(), 0, 1.3, 1, 0, 0), 5, 0.3, 0.2, 0.3, Material.REDSTONE_BLOCK.createBlockData());

                            t--;
                        }
                    }.runTaskTimer(plugin, 0, 1);
                }
            }.runTaskLater(plugin, 20);
        };
    }

    @Override
    public @NotNull String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TRIVIAL, 1, 1, 1, false);
    }
}
