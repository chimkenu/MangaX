package me.chimkenu.mangax.characters.tanjiro;

import me.chimkenu.mangax.ParticleEffects;
import me.chimkenu.mangax.SkullUtil;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

import static me.chimkenu.mangax.ArmorStandUtil.*;

public class FakeRainbow extends Move {
    public FakeRainbow() {
        super((plugin, player) -> {

            // Create stand
            ArmorStand stand = player.getWorld().spawn(player.getLocation(), ArmorStand.class);
            setUpArmorStand(stand);
            stand.setInvisible(false);
            stand.setArms(true);

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

            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 4, false, false, false));

            new BukkitRunnable() {
                int t = 60;
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (t <= 0) {
                        player.getWorld().spawnParticle(Particle.FLAME, player.getEyeLocation(), 20, 0.5, 0.3, 0.5, 0.1);
                        player.getWorld().spawnParticle(Particle.FLAME, stand.getEyeLocation(), 20, 0.5, 0.3, 0.5, 0.1);
                        cancel();
                        return;
                    }

                    player.getWorld().spawnParticle(Particle.FLAME, player.getLocation(), 1, 0.1, 0, 0.1, 0.05);

                    if (t % 10 == 0) {
                        Location loc = stand.getLocation();
                        loc.setPitch(0);
                        loc.add(0, 0.1, 0);
                        for (int i = 0; i < 20; i++) {
                            loc.setYaw(i * 18);
                            ParticleEffects.create(plugin, loc, 5, 10, (world, location) -> world.spawnParticle(Particle.FLAME, location, 0, 0, 1, 0, 0.1), 0);
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

        }, null, 0, 25 * 20, Material.FIRE_CORAL_FAN, Component.text("Fake Rainbow").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return null;
    }
}
