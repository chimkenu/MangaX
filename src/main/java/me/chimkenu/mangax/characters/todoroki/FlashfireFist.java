package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;

public class FlashfireFist extends Move {
    public FlashfireFist() {
        super((plugin, player) -> {
            ParticleEffects.create(plugin, player.getWorld(), player.getEyeLocation().toVector(), player.getEyeLocation().getDirection(), 3, 5, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.ORANGE, 0.8f)), Integer.MAX_VALUE);
            Location loc = player.getEyeLocation();
            loc.add(loc.getDirection().multiply(2));
            for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                if (e != player && !e.getType().equals(EntityType.ARMOR_STAND)) {
                    // Do more damage if entity is already on fire
                    double multiplier = 1;
                    if (e.getFireTicks() > 0) {
                        multiplier = 1.5;
                        e.getWorld().playSound(e, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 0.5f);
                        e.getWorld().spawnParticle(Particle.LARGE_SMOKE, e.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
                    } else {
                        e.getWorld().playSound(e, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5f, 1);
                        e.getWorld().spawnParticle(Particle.SMOKE, e.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
                    }

                    e.damage(6 * multiplier, player);
                    e.setFireTicks((int) (40 * multiplier));
                    e.setVelocity(e.getVelocity().add(player.getLocation().getDirection().multiply(multiplier)));
                }
            }
        }, null, 0, 5 * 20, Material.MAGMA_BLOCK, Component.text("Flashfire Fist").color(TextColor.color(0xff6200)).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
