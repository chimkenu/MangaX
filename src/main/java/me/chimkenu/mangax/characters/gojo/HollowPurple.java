package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.utils.BlockEffects;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import static me.chimkenu.mangax.utils.ArmorStandUtil.*;

public class HollowPurple {
    public static final double RADIUS = 1.5;

    public static void activate(JavaPlugin plugin, LivingEntity entity, ArmorStand one, ArmorStand two) {
        // add 30 sec cooldown if entity is player
        if (entity instanceof Player player) {
            player.setCooldown(Moves.GOJO_RED_REVERSAL.move.getMaterial(), 30 * 20);
            player.setCooldown(Moves.GOJO_COLLAPSING_BLUE.move.getMaterial(), 30 * 20);
        }


        ArmorStand purple = one.getWorld().spawn(one.getLocation(), ArmorStand.class);
        one.remove();
        two.remove();

        setUpArmorStand(purple);
        purple.setSmall(true);

        purple.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 80, 200, false, false, false));

        if (!purple.getLocation().subtract(new Vector(0, 0.2, 0)).getBlock().isPassable()) {
            purple.teleport(purple.getLocation().add(0, 1.2, 0));
        }

        new BukkitRunnable() {
            int t = 20;
            @Override
            public void run() {
                if (t <= 0) {
                    cancel();
                    return;
                }

                purple.getWorld().spawnParticle(Particle.END_ROD, purple.getLocation(), 5, 0.2, 0.2, 0.2, 0);
                purple.getWorld().spawnParticle(Particle.WITCH, purple.getLocation(), 35, 0.5, 0.5, 0.5, 0);
                Location diff = purple.getLocation();
                diff.add(0, -0.25, 0);
                purple.getWorld().spawnParticle(Particle.PORTAL, diff, 35, 0.5, 0.5, 0.5, 0);

                makeSphere(purple.getLocation(), Color.fromRGB(0x6417ac), 200, RADIUS);
                doBlockEffects(plugin, purple.getLocation());

                t--;
            }

            @Override
            public void cancel() {
                purple.setGravity(true);
                super.cancel();
                new BukkitRunnable() {
                    int t = 60;
                    @Override
                    public void run() {
                        if (t < 0) {
                            purple.remove();
                            cancel();
                            return;
                        }

                        purple.getWorld().spawnParticle(Particle.END_ROD, purple.getLocation(), 5, 0.2, 0.2, 0.2, 0);
                        purple.getWorld().spawnParticle(Particle.WITCH, purple.getLocation(), 35, 0.5, 0.5, 0.5, 0);
                        Location diff = purple.getLocation();
                        diff.add(0, -0.25, 0);
                        purple.getWorld().spawnParticle(Particle.PORTAL, diff, 35, 0.5, 0.5, 0.5, 0);

                        makeSphere(purple.getLocation(), Color.fromRGB(0x6417ac), 200, RADIUS);
                        doBlockEffects(plugin, purple.getLocation());

                        for (LivingEntity e : purple.getLocation().getNearbyLivingEntities(RADIUS)) {
                            if (e.hasGravity() && e != entity && e != purple) {
                                MoveTargetEvent event = new MoveTargetEvent(Moves.GOJO_RED_REVERSAL, entity, e, 12, new Vector());
                                Bukkit.getPluginManager().callEvent(event);
                                if (event.isCancelled()) {
                                    return;
                                }

                                e.damage(event.getDamage(), entity);
                            }
                        }

                        purple.setVelocity(entity.getLocation().getDirection().multiply(2));
                        t--;
                    }
                }.runTaskTimer(plugin, 1, 1);
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    public static void makeSphere(Location loc, Color color, int n, double radius) {
        Location origin = loc.clone();
        final double phi = 137.5;

        for (int i = 0; i < n; i++) {
            double y = 1 - (i / (n - 1d)) * 2;
            double r = Math.sqrt(1 - y * y);

            double theta = phi * i;

            double x = Math.cos(theta) * r;
            double z = Math.sin(theta) * r;

            x *= radius; y *= radius; z *= radius;

            loc.getWorld().spawnParticle(Particle.DUST, origin.clone().add(x, y, z), 1, 0.1, 0.1, 0.1, new Particle.DustOptions(color, 0.8f));
        }
    }

    private static void doBlockEffects(JavaPlugin plugin, Location loc) {
        Block block = loc.getBlock();

        for (int x = -2; x < 2; x++) {
            for (int y = 2; y > -2; y--) {
                for (int z = -2; z < 2; z++) {
                    Block rel = block.getRelative(x, y, z);
                    if (!rel.getRelative(0, -1, 0).isEmpty()) // only remove blocks that have another block below it
                        BlockEffects.create(plugin, rel.getLocation(), Material.AIR.createBlockData(), 5 * 20, location -> {});
                }
            }
        }
    }
}
