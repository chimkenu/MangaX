package me.chimkenu.mangax.characters.naruto;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class Dodge extends Move implements Listener {
    public Dodge() {
        super((plugin, entity) -> {
            entity.addScoreboardTag("naruto-dodge");
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.removeScoreboardTag("naruto-dodge");
                }
            }.runTaskLater(plugin, 20 * 5);
        }, null, 0, 25 * 20, Material.FEATHER, Component.text("Dodge").color(NamedTextColor.WHITE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.OTHER, MoveInfo.Type.BUFF, MoveInfo.Difficulty.TRIVIAL, 7, 1, 1, false);
    }

    @EventHandler
    public void onDamage(MoveTargetEvent e) {
        LivingEntity target = e.getTarget();
        if (target.getScoreboardTags().contains("naruto-dodge")) {
            if (e.getMove().move.getMoveInfo().type() == MoveInfo.Type.CONTROL) {
                target.removeScoreboardTag("naruto-dodge");
                return;
            }

            target.getWorld().spawnParticle(Particle.POOF, target.getEyeLocation(), 50, 0.1, 0.2, 0.1, 0.2);
            e.setCancelled(true);

            new BukkitRunnable() {
                @Override
                public void run() {
                    target.removeScoreboardTag("naruto-dodge");
                }
            }.runTaskLater(MangaX.getPlugin(MangaX.class), 2 * 20);
        }
    }
}
