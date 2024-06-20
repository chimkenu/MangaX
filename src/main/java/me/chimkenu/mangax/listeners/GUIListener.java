package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.events.GUICloseEvent;
import me.chimkenu.mangax.gui.GUI;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
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

        if (e.getClickedInventory() == null || e.getClickedInventory() != gui.getInventory() || e.getAction().toString().contains("DROP") || e.getAction().toString().contains("HOTBAR") || e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            e.setCancelled(true);
            return;
        }

        if (action == null) {
            return;
        }

        if (action.isFixed(e.getAction())) {
            e.setCancelled(true);
        }

        action.click(player);
    }

    @EventHandler
    public void onDrag(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) {
            return;
        }

        UUID player_uuid = player.getUniqueId();

        UUID inventory_uuid = GUI.openInventories.get(player_uuid);
        if (inventory_uuid == null) {
            return;
        }

        if (e.getInventorySlots().size() > 1) {
            e.setCancelled(true);
            return;
        }

        GUI gui = GUI.inventoriesByUUID.get(inventory_uuid);
        GUI.Action action = gui.getActions().get(e.getInventorySlots().iterator().next());

        if (e.getInventory() != gui.getInventory()) {
            e.setCancelled(true);
            return;
        }

        if (action == null) {
            return;
        }

        if (action.isFixed(InventoryAction.PLACE_ALL)) {
            e.setCancelled(true);
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

        if (gui.onClose(event.getPlayer()))
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
