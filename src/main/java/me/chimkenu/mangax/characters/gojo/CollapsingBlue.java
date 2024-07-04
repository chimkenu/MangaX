package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;
import static me.chimkenu.mangax.utils.ArmorStandUtil.setUpArmorStand;

public class CollapsingBlue extends Move {
    public CollapsingBlue() {
        super(null, null, 20 * 10, 10, Material.BLUE_CONCRETE, Component.text(""));

        this.activate = (plugin, entity) -> {
            Location loc = getRelativeLocation(entity.getEyeLocation(), 0, 0, 2, 0, 0);
            ArmorStand stand = loc.getWorld().spawn(loc, ArmorStand.class);
            setUpArmorStand(stand);

            new BukkitRunnable() {
                int t = getFollowUpTime();
                @Override
                public void run() {
                    if (t <= 0) {
                        cancel();
                        return;
                    }

                    stand.getWorld().spawnParticle(Particle.DUST, stand.getLocation(), 25, 0.1, 0.1, 0.1, 0, new Particle.DustOptions(Color.BLUE, 0.7f));
                    for (LivingEntity e : stand.getLocation().getNearbyLivingEntities(5)) {
                        if (!e.hasGravity()) {
                            continue;
                        }

                        Vector direction = e.getLocation().toVector().subtract(entity.getLocation().toVector());
                        direction = direction.normalize();
                        Vector v = e.getVelocity().add(direction.multiply(1.5)).add(new Vector(0, 0.2, 0));

                        MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_RED_REVERSAL, entity, e, 0, v);
                        Bukkit.getPluginManager().callEvent(event);
                        if (event.isCancelled()) {
                            return;
                        }

                        e.setVelocity(e.getVelocity().add(event.getKnockback()));
                    }

                    t--;
                }
            }.runTaskTimer(plugin, 0, 1);
        };
    }

    @Override
    public String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return null;
    }
}
