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

public class DetroitSmash extends Move {
    public DetroitSmash() {
        super((plugin, entity) -> {
            Location loc = entity.getEyeLocation();
            ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 5, 10, (world, location, index) -> world.spawnParticle(Particle.SPIT, location, 1, 0, 0, 0, 0), 0);

            loc.add(loc.getDirection().multiply(2));
            for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                if (e != entity && !e.getType().equals(EntityType.ARMOR_STAND)) {
                    MoveTargetEvent event = new MoveTargetEvent(Moves.DEKU_DELAWARE_SMASH, entity, e, 12, new Vector());
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }
                    e.setVelocity(e.getVelocity().add(event.getKnockback()));
                    e.damage(event.getDamage(), entity);
                }
            }
        }, null, 0, 15 * 20, Material.RAW_IRON, Component.text("Detroit Smash").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
