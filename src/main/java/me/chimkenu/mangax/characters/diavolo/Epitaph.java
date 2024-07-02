package me.chimkenu.mangax.characters.diavolo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class Epitaph extends Move implements Listener {
    public Epitaph() {
        super((plugin, entity) -> {
            entity.addScoreboardTag("diavolo-epitaph");
            Location loc = entity.getLocation();
            loc.setPitch(0);
            for (int i = 0; i < 20; i++) {
                loc.setYaw(i * 18);
                ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 2, 20, (world, location, index) -> {
                    Location diff = entity.getLocation().subtract(loc);
                    location.add(diff.add(0, 0.2, 0));
                    world.spawnParticle(Particle.DUST, getRelativeLocation(location, Math.log(index), 0, 0, 0, 0), 1, 0.05, 0.05, 0.05, 0.1, new Particle.DustOptions(Color.RED, 0.8f));
                }, 0);
            }
        }, null, 0, 15 * 20, Material.RED_DYE, Component.text("Epitaph").color(NamedTextColor.DARK_RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.OTHER, MoveInfo.Type.MANOEUVRE, MoveInfo.Difficulty.TYPICAL, 10, 1, 1, false);
    }

    @EventHandler
    public void onDamage(MoveTargetEvent e) {
        LivingEntity target = e.getTarget();
        if (!target.getScoreboardTags().contains("diavolo-epitaph")) {
            return;
        }

        if (e.getDamage() <= 0 && e.getMove().move.getMoveInfo().type() != MoveInfo.Type.CONTROL) {
            return;
        }

        target.removeScoreboardTag("diavolo-epitaph");
        Location loc = getRelativeLocation(e.getSource().getLocation(), 0, 1, -1.5, 0, 0);
        if (!loc.getBlock().isPassable()) {
            loc = e.getSource().getLocation();
        }
        target.teleport(loc);

        e.setCancelled(true);
    }
}
