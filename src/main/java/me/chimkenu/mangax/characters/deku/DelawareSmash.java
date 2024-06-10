package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DelawareSmash extends Move {
    public DelawareSmash() {
        super((plugin, entity) -> {
            Location loc = entity.getEyeLocation();
            loc.setY(loc.getY() - 0.3);
            for (int i = 0; i < 7; i++) {
                loc.setYaw(entity.getYaw() - 50);
                for (int j = 0; j < 42; j++) {
                    ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 25, 10, new ParticleEffects.Effect() {
                        @Override
                        public void playParticle(World world, Location location, int index) {
                            world.spawnParticle(Particle.SPIT, location, 1, 0, 0, 0, 0);
                        }

                        @Override
                        public void intersect(LivingEntity livingEntity) {
                            if (livingEntity != entity && !livingEntity.getType().equals(EntityType.ARMOR_STAND) && livingEntity.getNoDamageTicks() <= 0) {
                                Vector v = livingEntity.getLocation().toVector().subtract(entity.getLocation().toVector());
                                v = v.normalize().multiply(2).add(new Vector(0, 0.2, 0));

                                MoveTargetEvent event = new MoveTargetEvent(Moves.DEKU_DELAWARE_SMASH, entity, livingEntity, 8, v);
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return;
                                }
                                livingEntity.setVelocity(livingEntity.getVelocity().add(event.getKnockback()));
                                livingEntity.damage(event.getDamage(), entity);

                                livingEntity.setNoDamageTicks(15);
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
