package me.chimkenu.mangax.characters;

import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.gui.GUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Move {
    protected Activate activate;
    protected Activate followUp;
    private final int followUpTime;
    private final int cooldown;
    private final Material material;
    private final Component name;

    /**
     * Creates a move that is used in game
     * @param activate what will run on the first click
     * @param followUp what will run after {@code getFollowUpTime()} number of ticks
     * @param followUpTime time in ticks between activation and follow up
     * @param cooldown time in ticks between follow up and next activate
     * @param material the material (item) for the corresponding move
     * @param name display name of item
     */
    public Move(@Nullable Activate activate, @Nullable Activate followUp, int followUpTime, int cooldown, @NotNull Material material, @NotNull Component name) {
        this.activate = activate;
        this.followUp = followUp;
        this.followUpTime = followUpTime;
        this.cooldown = cooldown;
        this.material = material;
        this.name = name;
    }

    public @NotNull Activate getActivate() {
        return activate;
    }

    public @Nullable Activate getFollowUp() {
        return followUp;
    }

    public int getFollowUpTime() {
        return followUpTime;
    }

    public int getCooldown() {
        return cooldown;
    }

    public @NotNull Material getMaterial() {
        return material;
    }

    public @NotNull Component getName() {
        return name;
    }

    /**
     * Returns the lore of a move
     * @return a string array with the move's lore
     */
    public abstract @NotNull String[] getLore();

    /**
     * Generates a copy of the move
     * @return an ItemStack
     */
    public @NotNull ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = GUI.metaWithLore(item, getLore());
        meta.displayName(getName());
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Returns the MoveInfo of a move - this is used to let the AI know how to use the move
     * @return the move's MoveInfo
     */
    public abstract @NotNull MoveInfo getMoveInfo();
}
