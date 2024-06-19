package me.chimkenu.mangax.characters.goku;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

public class Dash extends Move {
    public Dash() {
        super((plugin, entity) -> {
            entity.setVelocity(entity.getVelocity().add(entity.getLocation().getDirection().multiply(3)).add(new Vector(0, 0.2, 0)));
            new BukkitRunnable() {
                final HashSet<LivingEntity> targets = new HashSet<>();
                int t = 10;

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

                    for (LivingEntity e : entity.getEyeLocation().getNearbyLivingEntities(2)) {
                        if (e != entity && !e.getType().equals(EntityType.ARMOR_STAND)) {
                            if (targets.contains(e)) {
                                continue;
                            }
                            targets.add(e);

                            MoveTargetEvent event = new MoveTargetEvent(Moves.GOKU_DASH, entity, e, 4, new Vector());
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                continue;
                            }
                            e.damage(event.getDamage(), entity);
                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
                        }
                    }
                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        }, null, 0, 15 * 20, Material.SPECTRAL_ARROW, Component.text("Dash").color(NamedTextColor.YELLOW).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.LOW, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.FORWARD, MoveInfo.Type.AREA, MoveInfo.Difficulty.TYPICAL, 7, 1, 10, false);
    }
}
