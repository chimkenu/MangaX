package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.enums.Moves;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class MoveListener extends GameListener {
    public MoveListener(JavaPlugin plugin) {
        super(plugin);
    }

    private boolean activateMove(Player player) {
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == null) {
            return false;
        }

        if (player.getCooldown(move.move.getMaterial()) > 0) {
            return false;
        }

        move.move.getActivate().activate(plugin, player);
        player.setCooldown(move.move.getMaterial(), move.move.getCooldown());
        return true;
    }

    @EventHandler
    public void onLeftClickNothing(PlayerInteractEvent e) {
        if (!(e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
            return;
        }
        e.setCancelled(activateMove(e.getPlayer()));
    }

    @EventHandler
    public void onLeftClickEntity(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player)) {
            return;
        }
        e.setCancelled(activateMove(player));
    }
}
