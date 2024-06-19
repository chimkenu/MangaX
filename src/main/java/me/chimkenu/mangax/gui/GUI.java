package me.chimkenu.mangax.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GUI {
    private final Inventory inventory;
    private final Map<Integer, Action> actions;
    private final UUID uuid;
    private final boolean isFixed;

    public static final Map<UUID, GUI> inventoriesByUUID = new HashMap<>();
    public static final Map<UUID, UUID> openInventories = new HashMap<>();

    public GUI(int size, Component name, boolean isFixed) {
        uuid = UUID.randomUUID();
        inventory = Bukkit.createInventory(null, size, name);
        actions = new HashMap<>();
        this.isFixed = isFixed;
        inventoriesByUUID.put(getUuid(), this);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setItem(int slot, ItemStack item, Action action) {
        inventory.setItem(slot, item);
        if (action != null) {
            actions.put(slot, action);
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

    public void delete() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID uuid = openInventories.get(player.getUniqueId());
            if (uuid.equals(getUuid())) {
                player.closeInventory();
            }
        }
        inventoriesByUUID.remove(getUuid());
    }

    public static Map<UUID, GUI> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public Map<Integer, Action> getActions() {
        return actions;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public interface Action {
        void click(Player player);
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
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack newGUIItem(Material material, Component displayName, boolean isGlowing) {
        return newGUIItem(material, displayName, isGlowing, 1);
    }

    public ItemStack newGUIItem(Material material, Component displayName) {
        return newGUIItem(material, displayName, false);
    }

    public static ItemStack newItem(Material material, Component displayName, boolean isGlowing, int amount) {
        ItemStack item = new ItemStack(material);
        item.setAmount(amount);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(displayName);
            if (isGlowing) {
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(meta);
        }
        return item;
    }
}
