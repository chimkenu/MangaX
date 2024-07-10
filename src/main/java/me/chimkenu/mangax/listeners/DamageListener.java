package me.chimkenu.mangax.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;

public class DamageListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<Player, BukkitTask> tasks;

    public DamageListener(JavaPlugin plugin) {
        this.plugin = plugin;
        tasks = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;

        int hearts = (int) Math.floor(Math.round(e.getFinalDamage()) / 2d);
        if (hearts > 0) {
            Location loc = e.getEntity().getLocation();
            loc.add(0, 1.6, 0);
            loc.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, loc, hearts, 0.2, 0.2, 0.2, 0.1);
        }

        if (e.getEntity() instanceof Player player) {
            int threshold = 10;
            if (player.getHealth() < threshold) {
                if (!tasks.containsKey(player)) {
                    tasks.put(player, new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (!player.isOnline() || player.isDead() || player.getHealth() > threshold) {
                                tasks.remove(player);
                                cancel();
                                return;
                            }
                            player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, player.getLocation(), (int) Math.round(threshold - player.getHealth()), 0.1, 0.3, 0.1, 0);
                        }
                    }.runTaskTimer(plugin, 0, 5));
                }
            }
        }
    }

    @EventHandler
    public void onSuffocationDamage(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
            e.setCancelled(true);
            Entity entity = e.getEntity();
            Location loc = entity.getLocation();
            while (!loc.getBlock().isEmpty() || !loc.getBlock().getRelative(0, 1, 0).isEmpty()) {
                loc.add(0, 2.5, 0);
            }
            entity.teleport(loc);
        }
    }
}
