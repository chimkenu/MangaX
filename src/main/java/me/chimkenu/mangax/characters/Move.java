package me.chimkenu.mangax.characters;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class Move {
    private final Activate activate;
    private final FollowUp followUp;
    private final int cooldown;
    private final Material material;
    private final Component name;
    private final ArrayList<Component> lore;

    public Move(Activate activate, FollowUp followUp, int cooldown, Material material, Component name, ArrayList<Component> lore) {
        this.activate = activate;
        this.followUp = followUp;
        this.cooldown = cooldown;
        this.material = material;
        this.name = name;
        this.lore = lore;
    }

    public Activate getActivate() {
        return activate;
    }

    public FollowUp getFollowUp() {
        return followUp;
    }

    public int getCooldown() {
        return cooldown;
    }

    public Material getMaterial() {
        return material;
    }

    public Component getName() {
        return name;
    }

    public ArrayList<Component> getLore() {
        return lore;
    }

    public ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(getName());
        meta.lore(getLore());
        item.setItemMeta(meta);
        return item;
    }
}
