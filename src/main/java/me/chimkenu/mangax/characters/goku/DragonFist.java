package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DragonFist extends Move {
    public DragonFist() {
        super((plugin, entity) -> {
            ParticleEffects.create(plugin, entity.getWorld(), entity.getEyeLocation().toVector(), entity.getEyeLocation().getDirection(), 3, 5, (world, location, index) -> world.spawnParticle(Particle.DUST, location, 50, 0.5, 0.5, 0.5, new Particle.DustOptions(Color.YELLOW, 0.8f)), Integer.MAX_VALUE);
            Location loc = entity.getEyeLocation();
            loc.add(loc.getDirection().multiply(2));
            for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                if (e != entity && !e.getType().equals(EntityType.ARMOR_STAND)) {
                    MoveTargetEvent event = new MoveTargetEvent(Moves.GOKU_DRAGON_FIST, entity, e, 6, new Vector());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    e.damage(event.getDamage(), entity);
                    e.setVelocity(e.getVelocity().add(event.getKnockback()));

                    e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0, false, false, false));
                    e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 4, false, false, false));
                    e.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 100, 4, false, false, false));
                }
            }
        }, null, 0, 10 * 20, Material.HORN_CORAL, Component.text("Dragon Fist!").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
