package me.chimkenu.mangax.characters.phoenix;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;

import static me.chimkenu.mangax.utils.ArmorStandUtil.getRelativeLocation;

public class HoldIt extends Move {
    public HoldIt() {
        super(null, null, 10, 20 * 10, Material.BEEHIVE, Component.text("Hold It!").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Location loc = entity.getLocation();
            loc.setPitch(0);
            loc.add(loc.getDirection().multiply(2));

            while (loc.getBlock().isPassable()) {
                loc.subtract(0, 0.01, 0);
            }
            loc.add(0, 0.01, 0);

            BlockDisplay table = entity.getWorld().spawn(loc, BlockDisplay.class);
            table.setBlock(getMaterial().createBlockData());
            table.setTransformation(new Transformation(new Vector3f(-1.5f, 9, -0.5f), new AxisAngle4f(), new Vector3f(3, 1, 1), new AxisAngle4f()));
            table.setInterpolationDelay(0);
            table.setInterpolationDuration(getFollowUpTime());

            new BukkitRunnable() {
                @Override
                public void run() {
                    table.setTransformation(new Transformation(new Vector3f(-1.5f, 0, -0.5f), new AxisAngle4f(), new Vector3f(3, 1, 1), new AxisAngle4f()));
                }
            }.runTaskLater(plugin, 2);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead()) {
                        table.remove();
                        return;
                    }

                    // Silly ground pound effect
                    table.getWorld().playSound(table.getLocation(), Sound.ENTITY_ZOMBIE_BREAK_WOODEN_DOOR, SoundCategory.PLAYERS, 0.5f, 0);
                    table.getWorld().spawnParticle(Particle.BLOCK, getRelativeLocation(table.getLocation(), 0, 0.5, 0, 0, 0), 100, 0.7, 0.3, 0.7, 1, getMaterial().createBlockData());
                    Location loc = table.getLocation();
                    loc.setPitch(0);
                    loc.add(0, 0.2, 0);
                    for (int i = 0; i < 20; i++) {
                        loc.setYaw(i * 18);
                        ParticleEffects.create(plugin, loc.getWorld(), loc.toVector(), loc.getDirection(), 6, 5, (world, location, index) -> world.spawnParticle(Particle.CRIT, location, 10, 0.25, 0.1, 0.25, 0.15), 0);
                    }

                    for (LivingEntity e : table.getLocation().getNearbyLivingEntities(5)) {
                        if (!e.getType().equals(EntityType.ARMOR_STAND) && e != entity) {
                            MoveTargetEvent event = new MoveTargetEvent(Moves.PHOENIX_HOLD_IT, entity, e, 6, new Vector());
                            Bukkit.getPluginManager().callEvent(event);
                            if (event.isCancelled()) {
                                continue;
                            }

                            e.damage(event.getDamage(), entity);
                            e.setVelocity(e.getVelocity().add(event.getKnockback()));
                        }
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 3; i++) {
                                table.getWorld().spawnParticle(Particle.BLOCK, getRelativeLocation(table.getLocation(), -1 + i, 0.5, 0, 0, 0), 50, 0.3, 0.3, 0.3, 1, getMaterial().createBlockData());
                            }
                            table.getWorld().playSound(table.getLocation(), Sound.BLOCK_WOOD_BREAK, SoundCategory.PLAYERS, 1, 0.5f);
                            table.remove();
                        }
                    }.runTaskLater(plugin, 20);
                }
            }.runTaskLater(plugin, getFollowUpTime());
        };
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.MEDIUM, MoveInfo.Range.MID, MoveInfo.Knockback.NORMAL, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.AREA, MoveInfo.Difficulty.TRIVIAL, 7, getFollowUpTime(), 1, false);
    }
}
