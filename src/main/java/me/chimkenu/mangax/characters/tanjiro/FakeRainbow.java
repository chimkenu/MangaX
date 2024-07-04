package me.chimkenu.mangax.characters.tanjiro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.newEulerAngle;
import static me.chimkenu.mangax.utils.ArmorStandUtil.setUpArmorStand;

public class FakeRainbow extends Move {
    public FakeRainbow() {
        super((plugin, entity) -> {

            // Create stand
            ArmorStand stand = entity.getWorld().spawn(entity.getLocation(), ArmorStand.class);
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

            entity.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 60, 0, false, false, false));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 4, false, false, false));

            new BukkitRunnable() {
                int t = 60;
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

                    if (t <= 0) {
                        entity.getWorld().spawnParticle(Particle.FLAME, entity.getEyeLocation(), 20, 0.5, 0.3, 0.5, 0.1);
                        cancel();
                        return;
                    }

                    entity.getWorld().spawnParticle(Particle.FLAME, entity.getLocation(), 1, 0.3, 0.1, 0.3, 0.1);
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
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.OTHER, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 10, 1, 60, false);
    }
}
