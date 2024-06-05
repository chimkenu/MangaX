package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class ShootStyleLeap extends Move {
    public ShootStyleLeap() {
        super((plugin, player) -> {
            // Silly jump effects
            Location loc = player.getLocation();
            loc.setY(loc.getY() + 0.1);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(plugin, loc, 5, 10, (world, location) -> world.spawnParticle(Particle.SMOKE, location, 2, 0, 0.5, 0, 0), 0);
            }

            // Launch player
            player.setVelocity(player.getVelocity().add(new Vector(0, 2, 0)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    Location loc = player.getLocation();
                    loc.setPitch(0);
                    player.setVelocity(player.getVelocity().add(loc.getDirection().multiply(3)));
                }
            }.runTaskLater(plugin, 1);

        }, (plugin, player) -> {
            player.setVelocity(player.getVelocity().add(new Vector(0, -2, 0)));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    // Silly ground pound effect
                    Location loc = player.getLocation();
                    loc.setPitch(0);
                    loc.add(0, 0.2, 0);
                    for (int i = 0; i < 20; i++) {
                        loc.setYaw(i * 18);
                        ParticleEffects.create(plugin, loc, 6, 5, (world, location) -> world.spawnParticle(Particle.CRIT, location, 10, 0.25, 0.1, 0.25, 0.15), 0);
                    }

                    // Ground pound damage
                    for (LivingEntity e : player.getLocation().getNearbyLivingEntities(5)) {
                        if (e instanceof LivingEntity l) {
                            if (!l.getType().equals(EntityType.ARMOR_STAND) && l != player) {
                                l.damage(3, player);
                            }
                        }
                    }
                }
            }.runTaskLater(plugin, 15);

        }, 12, 15 * 20, Material.LEATHER_HELMET, Component.text("Shoot Style Leap").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(Color.fromRGB(0x106761));
        meta.displayName(getName());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
