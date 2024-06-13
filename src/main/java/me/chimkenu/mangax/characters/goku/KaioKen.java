package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class KaioKen extends Move {
    public KaioKen() {
        super((plugin, entity) -> {
            entity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 400, 2, false, false, false));
            entity.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 400, 1, false, false, false));

            // Play effect while active
            new BukkitRunnable() {
                int t = 400;
                @Override
                public void run() {
                    if (entity instanceof Player player && !player.isOnline()) {
                        cancel();
                        return;
                    }

                    if (t <= 0 || entity.isDead()) {
                        cancel();
                        return;
                    }

                    entity.getWorld().spawnParticle(Particle.DUST, entity.getEyeLocation(), 10, 0.3, 0.7, 0.3, 0.2, new Particle.DustOptions(Color.RED, 0.8f));
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 25 * 20, Material.RED_DYE, Component.text("KAIO-KEN 10").color(NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 10, 1, 1, false);
    }
}
