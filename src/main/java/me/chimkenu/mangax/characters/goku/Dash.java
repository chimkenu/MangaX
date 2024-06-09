package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class Dash extends Move {
    public Dash() {
        super((plugin, player) -> {
            player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(3)).add(new Vector(0, 0.2, 0)));
            new BukkitRunnable() {
                int t = 10;

                @Override
                public void run() {
                    if (t <= 0 || player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }
                    for (LivingEntity e : player.getEyeLocation().getNearbyLivingEntities(2)) {
                        if (e != player && !e.getType().equals(EntityType.ARMOR_STAND)) {
                            MoveTargetEvent event = new MoveTargetEvent(Moves.GOKU_DASH, player, e, 4, new Vector());
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                return;
                            }
                            e.damage(event.getDamage(), player);
                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
                        }
                    }
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 15 * 20, Material.SPECTRAL_ARROW, Component.text("Dash").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
