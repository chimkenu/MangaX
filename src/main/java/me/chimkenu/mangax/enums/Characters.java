package me.chimkenu.mangax.enums;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

        String s = meta.getDisplayName();
        for (Characters character : Characters.values()) {
            if (character.toString().toLowerCase().contains(s.toLowerCase()))
                return character;
        }
        return null;
    }
}
