package me.chimkenu.mangax.characters;

import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NullMove extends Move {
    public NullMove() {
        super(null, null, -1, -1, Material.AIR, Component.text(""));
    }

    @Override
    public String[] getLore() {
        return null;
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return null;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return new ItemStack(Material.AIR);
    }
}
