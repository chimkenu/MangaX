package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class FullBlast extends Move {
    public FullBlast() {
        super((plugin, player) -> {
            Location loc = player.getEyeLocation();
            new BukkitRunnable() {
                int i = 5 * 20;

                @Override
                public void run() {
                    if (i < 0 || !player.isOnline() || player.isDead()) {
                        this.cancel();
                    }

                    if (i % 10 == 0) {
                        player.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, loc, 2000, 3, 1, 3, 0, null, true);
                    }

                    i--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 10 * 20, Material.FIREWORK_STAR, Component.text("Full Blast").color(TextColor.fromHexString("#106761")).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
