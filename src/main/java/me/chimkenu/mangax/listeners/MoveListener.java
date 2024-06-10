package me.chimkenu.mangax.listeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveListener extends GameListener {
    public MoveListener(JavaPlugin plugin) {
        super(plugin);

        for (Moves move : Moves.values()) {
            if (move.move instanceof Listener listener)
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
    }

    private void activateMove(Player player) {
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == null) {
            return;
        }

        Move m = move.move;
        if (player.getCooldown(m.getMaterial()) > m.getCooldown()) {
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
            if (cooldown > 0 && cooldown <= e.getMove().move.getCooldown()) {
                e.cancel(MoveTriggerEvent.CancelReason.IN_COOLDOWN);
            }
        }
    }

    @EventHandler
    public void onLeftClick(PlayerArmSwingEvent e) {
        activateMove(e.getPlayer());
    }
}
