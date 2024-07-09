package me.chimkenu.mangax.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public abstract class GUI {
    private final Inventory inventory;
    private final Map<Integer, Action> actions;
    private final UUID uuid;

    public static final Map<UUID, GUI> inventoriesByUUID = new HashMap<>();
    public static final Map<UUID, UUID> openInventories = new HashMap<>();

    public GUI(int size, Component name) {
        uuid = UUID.randomUUID();
        inventory = Bukkit.createInventory(null, size, name);
        actions = new HashMap<>();
        inventoriesByUUID.put(getUuid(), this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setItem(int slot, ItemStack item, Action action) {
        inventory.setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
        } else {
            actions.remove(slot);
        }
    }

    public void setItem(int slot, ItemStack item) {
        setItem(slot, item, null);
    }

    public void addItem(ItemStack item, Action action) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                setItem(i, item, action);
                break;
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void open(Player player) {
        player.openInventory(inventory);
        openInventories.put(player.getUniqueId(), getUuid());
    }

    public abstract boolean onClose(Player player);

    public void delete() {
        openInventories.forEach((player, inventory) -> {
            if (getUuid() == inventory) {
                Player p = Bukkit.getPlayer(player);
                if (p != null)
                    p.closeInventory();
            }
        });
        inventoriesByUUID.remove(getUuid());
    }

    public Map<Integer, Action> getActions() {
        return actions;
    }

    public interface Action {
        void click(Player player);
        default boolean isFixed(InventoryAction action) {
            return true;
        }
    }

    public ItemStack modifyItem(ItemStack item, Component displayName, boolean isGlowing) {
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName);
        if (isGlowing) {
            meta.addEnchant(Enchantment.UNBREAKING, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack newGUIItem(Material material, Component displayName, boolean isGlowing, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName.decoration(TextDecoration.ITALIC, false));
            if (isGlowing) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
        }
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack newGUIItem(Material material, Component displayName, boolean isGlowing) {
        return newGUIItem(material, displayName, isGlowing, 1);
    }

    public ItemStack newGUIItem(Material material, Component displayName) {
        return newGUIItem(material, displayName, false);
    }

    public static ItemMeta metaWithLore(ItemStack item, String... strings) {
        ItemMeta meta = item.getItemMeta();
        List<Component> lore = new ArrayList<>();
        for (String s : strings) {
            lore.add(MiniMessage.miniMessage().deserialize(s));
        }
        meta.lore(lore);
        return meta;
    }
}
