package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DelawareSmash extends Move {
    public DelawareSmash() {
        super((plugin, player) -> {
            Location loc = player.getEyeLocation();
            loc.setY(loc.getY() - 0.3);
            for (int i = 0; i < 7; i++) {
                loc.setYaw(player.getYaw() - 50);
                for (int j = 0; j < 42; j++) {
                    ParticleEffects.create(plugin, loc, 25, 10, new ParticleEffects.Effect() {
                        @Override
                        public void playParticle(World world, Location location) {
                            world.spawnParticle(Particle.SPIT, location, 1, 0, 0, 0, 0);
                        }

                        @Override
                        public void intersect(LivingEntity livingEntity) {
                            if (livingEntity != player) {
                                Vector v = livingEntity.getLocation().toVector().subtract(player.getLocation().toVector());
                                v = v.normalize().multiply(2).add(new Vector(0, 0.2, 0));
                                livingEntity.setVelocity(livingEntity.getVelocity().add(v));
                                livingEntity.damage(8, player);
                            }
                        }
                    }, 0);
                    loc.setYaw(loc.getYaw() + 2.5f);
                }
                loc.setPitch(loc.getPitch() + 2);
            }
        }, null, 0, 15 * 20, Material.RAW_GOLD, Component.text("Delaware Smash").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
