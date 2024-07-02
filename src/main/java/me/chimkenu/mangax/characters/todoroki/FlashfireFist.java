package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class FlashfireFist extends Move implements Punch {
    public FlashfireFist() {
        super((plugin, entity) -> ParticleEffects.create(plugin, entity.getWorld(), entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection(), 3, 5, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.ORANGE, 0.8f)), Integer.MAX_VALUE), null, 0, 5 * 20, Material.MAGMA_BLOCK, Component.text("Flashfire Fist").color(TextColor.color(0xff6200)).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.CLOSE, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TYPICAL,3, 1, 1, false);
    }

    @Override
    public void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp) {
        MoveTargetEvent event;

        // Do more damage if entity is already on fire
        double multiplier = 1;
        if (target.getFireTicks() > 0) {
            multiplier = 1.5;
            event = new MoveTargetEvent(Moves.TODOROKI_FLASHFIRE_FIST, source, target, 6 * multiplier, source.getLocation().getDirection().multiply(multiplier));
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.getWorld().playSound(target, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 1, 0.5f);
            target.getWorld().spawnParticle(Particle.LARGE_SMOKE, target.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
        } else {
            event = new MoveTargetEvent(Moves.TODOROKI_FLASHFIRE_FIST, source, target, 6 * multiplier, source.getLocation().getDirection().multiply(multiplier));
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            target.getWorld().playSound(target, Sound.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.5f, 1);
            target.getWorld().spawnParticle(Particle.SMOKE, target.getEyeLocation(), 100, 0.3, 0.2, 0.3, 0.1);
        }

        event.getTarget().damage(event.getDamage(), event.getSource());
        event.getTarget().setFireTicks((int) (40 * multiplier));
        target.setVelocity(target.getVelocity().add(event.getKnockback()));
    }
}
