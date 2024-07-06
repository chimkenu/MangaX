package me.chimkenu.mangax.gui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Controls extends GUI {
    public Controls() {
        super(9, Component.text("Controls").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.BOLD));

        ItemStack fist = newGUIItem(Material.WOODEN_SWORD, Component.text("Fist").color(NamedTextColor.WHITE).append(Component.text(" [Left Click]")).color(NamedTextColor.GRAY));
        ItemStack move = newGUIItem(Material.IRON_SWORD, Component.text("Move").color(NamedTextColor.WHITE).append(Component.text(" [Left Click]")).color(NamedTextColor.GRAY));
        ItemStack block = newGUIItem(Material.SHIELD, Component.text("Block").color(NamedTextColor.WHITE).append(Component.text(" [Sneak]")).color(NamedTextColor.GRAY));
        ItemStack dash = newGUIItem(Material.SHIELD, Component.text("Dash").color(NamedTextColor.WHITE).append(Component.text(" [Drop]")).color(NamedTextColor.GRAY));

        fist.setItemMeta(metaWithLore(fist, "Swing your fists at your opponent!",
                "This basic attack has a base damage of 1 heart."
        ));

        move.setItemMeta(metaWithLore(move, "You can equip 4 moves based on your",
                "selected character. These are found in",
                "your first 4 slots. Each move is unique and",
                "its description can be found in its lore."
        ));

        block.setItemMeta(metaWithLore(block, "Sneaking sets your off-hand to a shield.",
                "Each block can take up to 3 hearts of damage or",
                "3 hits before breaking. Attacks that deal more",
                "than 3 hearts break the block unless the block",
                "is timed right before the attack strikes.",
                "Perfect blocks stuns the attacker for a while,",
                "and attacks that break blocks deal 1.5x damage."
        ));

        dash.setItemMeta(metaWithLore(dash, "Dropping any item triggers a dash.",
                "Dashing is a movement ability which allows",
                "you to swiftly move in the direction you",
                "are moving. Dashing costs 3 hunger",
                "points, so be careful not to spam it or",
                "you won't be able to sprint! Hunger only",
                "regenerates when you are not sprinting."
        ));

        setItem(0, fist);
        setItem(1, move);
        setItem(2, block);
        setItem(3, dash);
    }

    @Override
    public boolean onClose(Player player) {
        return true;
    }
}
