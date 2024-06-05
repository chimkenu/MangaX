package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class KaioKen extends Move {
    public KaioKen() {
        super((plugin, player) -> {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 2, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 1, false, false, false));

            // Play effect while active
            new BukkitRunnable() {
                int t = 400;
                @Override
                public void run() {
                    if (t <= 0 || player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    player.getWorld().spawnParticle(Particle.DUST, player.getEyeLocation(), 10, 0.3, 0.7, 0.3, 0.2, new Particle.DustOptions(Color.RED, 0.8f));
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 25 * 20, Material.RED_DYE, Component.text("KAIO-KEN 10").color(NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
