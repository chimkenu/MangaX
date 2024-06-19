package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.events.GUICloseEvent;
import me.chimkenu.mangax.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import java.util.UUID;

public class GUIListener implements Listener {
    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        UUID player_uuid = player.getUniqueId();

        UUID inventory_uuid = GUI.openInventories.get(player_uuid);
        if (inventory_uuid == null) {
            return;
        }

        GUI gui = GUI.inventoriesByUUID.get(inventory_uuid);
        GUI.Action action = gui.getActions().get(e.getSlot());

        if (action == null) {
            return;
        }

        if (action.isFixed()) {
            e.setCancelled(true);
        }

        if (e.getClickedInventory() != null && e.getClickedInventory().getType() == InventoryType.PLAYER) {
            return;
        }

        action.click(player);
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        UUID guiUuid = GUI.openInventories.get(uuid);
        if (guiUuid == null) {
            return;
        }

        GUI gui = GUI.inventoriesByUUID.get(guiUuid);
        if (gui == null) {
            GUI.openInventories.remove(uuid);
            return;
        }

        GUICloseEvent event = new GUICloseEvent((Player) e.getPlayer(), gui);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            e.getPlayer().openInventory(gui.getInventory());
            return;
        }

        GUI.openInventories.remove(uuid);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        UUID uuid = e.getPlayer().getUniqueId();
        GUI.openInventories.remove(uuid);
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (e.getPlayer().getGameMode().equals(GameMode.ADVENTURE))
            e.setCancelled(true);
    }
}
