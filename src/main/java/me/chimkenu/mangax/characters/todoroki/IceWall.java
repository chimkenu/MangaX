package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.utils.BlockEffects;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class IceWall extends Move {
    public IceWall() {
        super(null, null, 0, 15 * 20, Material.BLUE_ICE, Component.text("Ice Wall").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, player) -> {
            int[] height = {7, 12, 15, 12, 7};
            for (int i = 0; i < height.length; i++) {
                Location loc = player.getEyeLocation();
                loc.setPitch(0);
                loc = getRelativeLocation(loc, -6 + (i * 3), -3, 4 - Math.abs(2 - i), 0, 0);
                loc.setPitch(-60);

                ParticleEffects.create(plugin, player.getWorld(), loc.toVector(), loc.getDirection(), height[i], 20, (world, location, index) -> {
                    for (Location l : getBlocksInRadius(location, 2.5 - index * 0.07)) {
                        BlockEffects.create(plugin, l, Material.BLUE_ICE.createBlockData(), 5 * 20, blockLoc -> {
                            blockLoc.add(0.5, 0.5, 0.5);
                            blockLoc.getWorld().spawnParticle(Particle.BLOCK, blockLoc, 5, 0.25, 0.25, 0.25, 0, Material.BLUE_ICE.createBlockData());
                            blockLoc.getWorld().playSound(blockLoc, Sound.BLOCK_GLASS_BREAK, 0.05f, 2);
                        });
                    }

                    for (LivingEntity e : location.getNearbyLivingEntities(3)) {
                        if (e.getNoDamageTicks() > 0) {
                            continue;
                        }

                        if (e.getType().equals(EntityType.ARMOR_STAND) || e == player) {
                            continue;
                        }

                        Vector direction = e.getLocation().toVector().subtract(player.getLocation().toVector());
                        direction = direction.normalize();
                        e.setVelocity(e.getVelocity().add(direction.multiply(1.5)).add(new Vector(0, 0.2, 0)));
                        e.damage(8, player);
                        e.setNoDamageTicks(20);
                    }
                }, Integer.MAX_VALUE);
            }
        };
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }

    private HashSet<Location> getBlocksInRadius(Location origin, double radius) {
        HashSet<Location> locations = new HashSet<>();

        int bx = origin.getBlockX();
        int bz = origin.getBlockZ();

        for (double x = bx - radius; x <= bx + radius; x++) {
            for (double z = bz - radius; z <= bz + radius; z++) {
                Location location = new Location(origin.getWorld(), x, origin.getY(), z).toBlockLocation();
                double distance = origin.distance(location);
                if (distance <= radius) {
                    locations.add(location);
                }
            }
        }

        return locations;
    }
}
