package me.chimkenu.mangax.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DamageListener implements Listener {
    private final JavaPlugin plugin;

    public DamageListener(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent e) {
        int hearts = (int) Math.floor(e.getFinalDamage() / 2);
        if (hearts > 0) {
            Location loc = e.getEntity().getLocation();
            loc.add(0, 1.6, 0);
            loc.getWorld().spawnParticle(Particle.DAMAGE_INDICATOR, loc, hearts, 0.2, 0.2, 0.2, 0.1);
        }

        if (e.getEntity() instanceof Player player) {
            int threshold = 10;
            if (player.getHealth() < threshold) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (player.isDead() || player.getHealth() > threshold) {
                            cancel();
                            return;
                        }
                        player.getWorld().spawnParticle(Particle.DRIPPING_DRIPSTONE_LAVA, player.getLocation(), 2, 0.1, 0.3, 0.1, 0);
                    }
                }.runTaskTimer(plugin, 0, 4);
            }
        }
    }
}
