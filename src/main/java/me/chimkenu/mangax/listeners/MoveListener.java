package me.chimkenu.mangax.listeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MoveListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> players;

    public MoveListener(JavaPlugin plugin) {
        this.plugin = plugin;
        for (Moves move : Moves.values()) {
            if (move.move instanceof Listener listener)
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        this.players = new HashMap<>();
    }

    private void activateMove(Player player) {
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == null) {
            return;
        }

        Move m = move.move;
        if (player.getCooldown(m.getMaterial()) > m.getCooldown()) {
            if (m.getFollowUp() == null)
                return;

            MoveTriggerEvent event = new MoveTriggerEvent(player, move, true);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            m.getFollowUp().activate(plugin, player);
            player.setCooldown(m.getMaterial(), m.getCooldown());
            return;
        }

        MoveTriggerEvent event = new MoveTriggerEvent(player, move, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        m.getActivate().activate(plugin, player);
        if (player.getCooldown(m.getMaterial()) == 0)
            player.setCooldown(m.getMaterial(), m.getCooldown() + m.getFollowUpTime());

        if (m.getFollowUp() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        return;
                    }

                    if (player.getCooldown(m.getMaterial()) == m.getCooldown()) {
                        MoveTriggerEvent followUp = new MoveTriggerEvent(player, move, true);
                        Bukkit.getPluginManager().callEvent(followUp);
                        if (followUp.isCancelled()) {
                            return;
                        }

                        m.getFollowUp().activate(plugin, player);
                    }
                }
            }.runTaskLater(plugin, m.getFollowUpTime() + 1);
        }
    }

    @EventHandler
    public void onMoveTrigger(MoveTriggerEvent e) {
        if (e.getEntity() instanceof Player player) {
            int cooldown = player.getCooldown(e.getMove().move.getMaterial());
            if (cooldown > 0 && cooldown < e.getMove().move.getCooldown()) {
                e.cancel(MoveTriggerEvent.CancelReason.IN_COOLDOWN);
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerArmSwingEvent e) {
        Long data = players.get(e.getPlayer().getUniqueId());
        if (data != null) {
            long diff = System.currentTimeMillis() - data;
            if (diff <= 10) return;
        }

        activateMove(e.getPlayer());
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        players.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }
}
