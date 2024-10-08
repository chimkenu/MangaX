package me.chimkenu.mangax.characters.todoroki;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.BlockEffects;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class IceWall extends Move {
    public IceWall() {
        super(null, null, 0, 15 * 20, Material.BLUE_ICE, Component.text("Ice Wall").color(NamedTextColor.BLUE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Location damageLoc = entity.getLocation();
            damageLoc.setPitch(0);

            Location[] locations = new Location[] {
                    getRelativeLocation(damageLoc, 0, 1, 5, 0, 0),
                    getRelativeLocation(damageLoc, 4, 1, 4, 0, 0),
                    getRelativeLocation(damageLoc, -4, 1, 4, 0, 0)
            };

            final HashSet<LivingEntity> targets = new HashSet<>();
            for (Location l : locations) {
                for (LivingEntity e : l.getNearbyLivingEntities(3)) {
                    if (e.getType().equals(EntityType.ARMOR_STAND) || e == entity) {
                        continue;
                    }

                    if (targets.contains(e)) {
                        continue;
                    }
                    targets.add(e);

                    Vector direction = e.getLocation().toVector().subtract(entity.getLocation().toVector());
                    direction = direction.normalize();
                    Vector v = direction.multiply(3).add(new Vector(0, 1, 0));

                    MoveTargetEvent event = new MoveTargetEvent(Moves.TODOROKI_ICE_WALL, entity, e, 4, v);
                    Bukkit.getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        return;
                    }

                    event.getTarget().setVelocity(event.getTarget().getVelocity().add(event.getKnockback()));
                    event.getTarget().damage(event.getDamage(), event.getSource());
                    event.getTarget().setNoDamageTicks(20);
                }
            }

            int[] height = {7, 12, 15, 12, 7};
            for (int i = 0; i < height.length; i++) {
                Location loc = entity.getEyeLocation();
                loc.setPitch(0);
                loc = getRelativeLocation(loc, -6 + (i * 3), -3, 4 - Math.abs(2 - i), 0, 0);
                loc.setPitch(-60);

                ParticleEffects.create(plugin, entity.getWorld(), loc.toVector(), loc.getDirection(), height[i], 20, (world, location, index) -> {
                    for (Location l : getBlocksInRadius(location, 2.5 - index * 0.07)) {
                        BlockEffects.create(plugin, l, Material.BLUE_ICE.createBlockData(), 5 * 20, blockLoc -> {
                            blockLoc.add(0.5, 0.5, 0.5);
                            blockLoc.getWorld().spawnParticle(Particle.BLOCK, blockLoc, 5, 0.25, 0.25, 0.25, 0, Material.BLUE_ICE.createBlockData());
                            blockLoc.getWorld().playSound(blockLoc, Sound.BLOCK_GLASS_BREAK, 0.05f, 2);
                        });
                    }

                }, Integer.MAX_VALUE);
            }
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.MID, MoveInfo.Knockback.HIGH, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TRIVIAL, 7, 3, 17, false);
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
