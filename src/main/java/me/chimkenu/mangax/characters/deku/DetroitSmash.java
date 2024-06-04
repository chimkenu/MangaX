package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;

public class DetroitSmash extends Move {
    public DetroitSmash() {
        super((plugin, player) -> {
            Location loc = player.getEyeLocation();
            ParticleEffects.create(plugin, loc, 5, 10, (world, location) -> world.spawnParticle(Particle.SPIT, location, 1, 0, 0, 0, 0), 0);

            loc.add(loc.getDirection().multiply(2));
            for (LivingEntity e : loc.getNearbyLivingEntities(1)) {
                if (e != player)
                    e.damage(12, player);
            }
        }, null, 0, 15 * 20, Material.RAW_IRON, Component.text("Detroit Smash").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
