package me.chimkenu.mangax.enums;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

public enum Characters {
    JOTARO,
    TANJIRO,
    GOKU,
    NARUTO,
    DEKU,
    DIAVOLO,
    TODOROKI,
    PHOENIX;

    public static Characters getCharacterFromItem(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null || !meta.hasDisplayName())
            return null;

        String s = Objects.requireNonNull(meta.displayName()).examinableName();
        for (Characters character : Characters.values()) {
            if (character.toString().toLowerCase().contains(s.toLowerCase()))
                return character;
        }
        return null;
    }
}
