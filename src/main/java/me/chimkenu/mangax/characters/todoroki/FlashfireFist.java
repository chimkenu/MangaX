package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class FlashfireFist extends Move {
    public FlashfireFist() {
        super((plugin, entity) -> {
            ParticleEffects.create(plugin, entity.getWorld(), entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection(), 3, 5, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.ORANGE, 0.8f)), Integer.MAX_VALUE);
            Location loc = entity.getEyeLocation();
            loc.add(loc.getDirection().multiply(2));
            for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                if (e != entity && !e.getType().equals(EntityType.ARMOR_STAND)) {
                    MoveTargetEvent event;

                    // Do more damage if entity is already on fire
                    double multiplier = 1;
                    if (e.getFireTicks() > 0) {
                        multiplier = 1.5;
                        event = new MoveTargetEvent(Moves.TODOROKI_FLASHFIRE_FIST, entity, e, 6 * multiplier, entity.getLocation().getDirection().multiply(multiplier));
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        e.getWorld().playSound(e, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 0.5f);
                        e.getWorld().spawnParticle(Particle.LARGE_SMOKE, e.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
                    } else {
                        event = new MoveTargetEvent(Moves.TODOROKI_FLASHFIRE_FIST, entity, e, 6 * multiplier, entity.getLocation().getDirection().multiply(multiplier));
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        e.getWorld().playSound(e, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5f, 1);
                        e.getWorld().spawnParticle(Particle.SMOKE, e.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
                    }

                    event.getTarget().damage(event.getDamage(), event.getSource());
                    event.getTarget().setFireTicks((int) (40 * multiplier));
                    e.setVelocity(e.getVelocity().add(event.getKnockback()));
                }
            }
        }, null, 0, 5 * 20, Material.MAGMA_BLOCK, Component.text("Flashfire Fist").color(TextColor.color(0xff6200)).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.CLOSE, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL,3, 1, 1, false);
    }
}
