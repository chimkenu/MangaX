package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class DragonFist extends Move implements Punch {
    public DragonFist() {
        super((plugin, entity) -> {
            ParticleEffects.create(plugin, entity.getWorld(), entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection(), 3, 5, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.YELLOW, 0.8f)), Integer.MAX_VALUE);
            Location loc = entity.getEyeLocation();
            loc.add(loc.getDirection().multiply(2));
        }, null, 0, 10 * 20, Material.HORN_CORAL, Component.text("Dragon Fist!").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.CLOSE, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.DEBUFF, MoveInfo.Difficulty.TYPICAL, 3, 1, 1, false);
    }

    @Override
    public void punch(JavaPlugin plugin, LivingEntity source, LivingEntity target, boolean isFollowUp) {
        MoveTargetEvent event = new MoveTargetEvent(Moves.GOKU_DRAGON_FIST, source, target, 6, new Vector());
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        target.damage(event.getDamage(), source);
        target.setVelocity(target.getVelocity().add(event.getKnockback()));

        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, false, false, false));
        target.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 4, false, false, false));
    }
}
