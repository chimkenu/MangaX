package me.chimkenu.mangax.listeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.Moves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class MoveListener extends GameListener {
    public MoveListener(JavaPlugin plugin) {
        super(plugin);
    }

    private void activateMove(Player player) {
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == null) {
            return;
        }

        Move m = move.move;
        if (player.getCooldown(m.getMaterial()) > m.getCooldown()) {
            m.getFollowUp().activate(plugin, player);
            player.setCooldown(m.getMaterial(), m.getCooldown());
            return;
        }

        if (player.getCooldown(m.getMaterial()) > 0) {
            return;
        }

        m.getActivate().activate(plugin, player);
        if (player.getCooldown(m.getMaterial()) == 0)
            player.setCooldown(m.getMaterial(), m.getCooldown() + m.getFollowUpTime());

        if (m.getFollowUpTime() > 0) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        this.cancel();
                        return;
                    }

                    if (player.getCooldown(m.getMaterial()) == m.getCooldown()) {
                        m.getFollowUp().activate(plugin, player);
                    }
                }
            }.runTaskLater(plugin, m.getFollowUpTime() + 1);
        }
    }

    @EventHandler
    public void onLeftClick(PlayerArmSwingEvent e) {
        activateMove(e.getPlayer());
    }
}
