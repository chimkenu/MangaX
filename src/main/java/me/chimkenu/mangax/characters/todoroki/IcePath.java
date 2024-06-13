package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.utils.BlockEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

public class IcePath extends Move {
    public IcePath() {
        super(null, null, 0, 15 * 20, Material.PACKED_ICE, Component.text("Ice Path").color(NamedTextColor.AQUA).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> new BukkitRunnable() {
            int t = 100;
            @Override
            public void run() {
                if (t <= 0 || entity.isDead() || (entity instanceof Player player && !player.isOnline())) {
                    cancel();
                    return;
                }

                Location l = entity.getLocation();
                l.setY(l.getY() - 1);
                for (Location loc : getBlocksInRadius(l)) {
                    BlockEffects.create(plugin, loc, Material.PACKED_ICE.createBlockData(), 3 * 20, blockLoc -> {
                        blockLoc.add(0.5, 0.5, 0.5);
                        blockLoc.getWorld().spawnParticle(Particle.BLOCK, blockLoc, 5, 0.25, 0.25, 0.25, 0, Material.BLUE_ICE.createBlockData());
                        blockLoc.getWorld().playSound(blockLoc, Sound.BLOCK_GLASS_BREAK, 0.05f, 2);
                    });
                }
                for (LivingEntity e : l.getNearbyLivingEntities(2, 1, 2)) {
                    Vector direction = e.getLocation().toVector().subtract(entity.getLocation().toVector());
                    direction = direction.normalize();
                    e.setVelocity(e.getVelocity().add(direction.multiply(1.5)).add(new Vector(0, 0.2, 0)));
                }
                entity.setVelocity(entity.getVelocity().add(entity.getLocation().getDirection().multiply(0.075)));

                t--;
            }

            private HashSet<Location> getBlocksInRadius(Location origin) {
                HashSet<Location> locations = new HashSet<>();
                int bx = origin.getBlockX();
                int bz = origin.getBlockZ();

                for (int x = bx - 2; x <= bx + 2; x++) {
                    for (int z = bz - 2; z <= bz + 2; z++) {
                        Location location = new Location(origin.getWorld(), x, origin.getY(), z).toBlockLocation();
                        double distance = origin.distanceSquared(location);
                        if (distance < 2 * 2) {
                            locations.add(location);
                        }
                    }
                }

                return locations;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public ArrayList<Component> getLore() {
        return null;
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.FORWARD, MoveInfo.Type.MANOEUVRE, MoveInfo.Difficulty.TRIVIAL,10, 1, 100, true);
    }
}
