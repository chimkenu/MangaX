package me.chimkenu.mangax.characters;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public abstract class Move {
    protected Activate activate;
    protected Activate followUp;
    private final int followUpTime;
    private final int cooldown;
    private final Material material;
    private final Component name;

    public Move(Activate activate, Activate followUp, int followUpTime, int cooldown, Material material, Component name) {
        this.activate = activate;
        this.followUp = followUp;
        this.followUpTime = followUpTime;
        this.cooldown = cooldown;
        this.material = material;
        this.name = name;
    }

    public Activate getActivate() {
        return activate;
    }

    public Activate getFollowUp() {
        return followUp;
    }

    public int getFollowUpTime() {
        return followUpTime;
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

    public abstract ArrayList<Component> getLore();

    public ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.displayName(getName());
        meta.lore(getLore());
        item.setItemMeta(meta);
        return item;
    }
}
