package me.chimkenu.mangax.characters.diavolo;

import me.chimkenu.mangax.utils.ParticleEffects;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Epitaph extends Move implements Listener {
    public Epitaph() {
        super((plugin, player) -> {
            player.addScoreboardTag("diavolo-epitaph-" + System.currentTimeMillis());
            Location loc = player.getLocation();
            loc.setPitch(0);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 2, 20, (world, location, index) -> {
                    Location diff = player.getLocation().subtract(loc);
                    location.add(diff.add(0, 0.2, 0));
                    world.spawnParticle(Particle.DUST, getRelativeLocation(location, Math.log(index), 0, 0, 0, 0), 1, 0.05, 0.05, 0.05, 0.1, new Particle.DustOptions(Color.RED, 0.8f));
                }, 0);
            }
        }, (plugin, player) -> {

        }, 25 * 20, 1, Material.RED_DYE, Component.text("Epitaph").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    @EventHandler
    public void onDamage(MoveTargetEvent e) {

    }
}
