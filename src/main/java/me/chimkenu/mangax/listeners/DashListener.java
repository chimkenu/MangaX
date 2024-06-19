package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.events.DashEvent;
import me.chimkenu.mangax.utils.ParticleEffects;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;

public class DashListener implements Listener {
    public static int DASH_COST = 6;
    public static int REGEN_RATE = 10;

    public static boolean dash(Vector velocity, LivingEntity entity) {
        DashEvent dashEvent = new DashEvent(entity);
        Bukkit.getPluginManager().callEvent(dashEvent);
        if (dashEvent.isCancelled()) {
            return false;
        }

        double len = velocity.lengthSquared();
        if (len == 0 || Double.isNaN(len))
            velocity = entity.getLocation().getDirection();
        entity.setVelocity(velocity);
        entity.getWorld().playSound(entity.getLocation(), Sound.ITEM_ARMOR_EQUIP_ELYTRA, SoundCategory.PLAYERS, 0.3f, 1);
        entity.getWorld().spawnParticle(Particle.CLOUD, entity.getLocation(), 10, 0.1, 0.1, 0.1, 0.01);
        ParticleEffects.create(MangaX.getPlugin(MangaX.class), entity.getWorld(), entity.getLocation().toVector(), velocity, 5, 10, (world, location, index) -> world.spawnParticle(Particle.CLOUD, location, 1, 0, 0, 0, 0), 0);
        return true;
    }

    private final JavaPlugin plugin;
    private final HashMap<UUID, BukkitTask> foodLevelRegeneration;

    public DashListener(JavaPlugin plugin) {
        this.plugin = plugin;
        foodLevelRegeneration = new HashMap<>();
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();
        if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
            return;
        }

        e.setCancelled(true);
        if (player.getFoodLevel() >= DASH_COST) {
            player.setFoodLevel(player.getFoodLevel() - DASH_COST);

            Vector origin = player.getLocation().toVector();
            new BukkitRunnable() {
                @Override
                public void run() {
                    Vector result = player.getLocation().toVector().subtract(origin);
                    result.setY(0);
                    result.add(result.clone().normalize());
                    dash(result, player);
                }
            }.runTaskLater(plugin, 1);
        }
        regenerate(player);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent e) {
        if (e.getEntity() instanceof Player player) {
            regenerate(player);
        }
    }

    @EventHandler
    public void onRegenerate(EntityRegainHealthEvent e) {
        if (e.getEntity() instanceof Player player && e.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            player.setFoodLevel(player.getFoodLevel() - 4);
            regenerate(player);
        }
    }

    public void regenerate(Player player) {
        if (foodLevelRegeneration.containsKey(player.getUniqueId())) {
            return;
        }

        foodLevelRegeneration.put(player.getUniqueId(), new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || player.isDead() || player.getFoodLevel() == 20) {
                    foodLevelRegeneration.remove(player.getUniqueId());
                    cancel();
                    return;
                }

                if (!player.isSprinting())
                    player.setFoodLevel(Math.min(20, player.getFoodLevel() + 1));
            }
        }.runTaskTimer(plugin, REGEN_RATE, REGEN_RATE));
    }
}
