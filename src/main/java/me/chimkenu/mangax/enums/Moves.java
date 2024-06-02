package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Moves {
    JOTARO_STAND_BARRAGE,
    JOTARO_HEAVY_HIT,
    JOTARO_STAND_JUMP,
    JOTARO_ZA_WARUDO,
    TANJIRO_DROP_RIPPLE_THRUST,
    TANJIRO_WATER_WHEEL,
    TANJIRO_STRIKING_TIDE,
    TANJIRO_FAKE_RAINBOW,
    GOKU_DRAGON_FIST,
    GOKU_DASH,
    GOKU_KAIO_KEN_10,
    GOKU_KAMEHAMEHA,
    NARUTO_RASENSHURIKEN,
    NARUTO_MULTI_SHADOW_CLONE_JUTSU,
    NARUTO_DODGE,
    NARUTO_RASENGAN,
    DEKU_DETROIT_SMASH,
    DEKU_DELAWARE_SMASH,
    DEKU_SHOOT_STYLE_LEAP,
    DEKU_FULL_BLAST,
    DIAVOLO_CRIMSON_BARRAGE,
    DIAVOLO_IMPALE,
    DIAVOLO_EPITAPH,
    DIAVOLO_TIME_SKIP;

    public Move move;

    public static Moves getMoveFromItem(ItemStack item) {
        for (Moves move : Moves.values()) {
            if (item.getType().equals(move.move.getMaterial())) {
                Component name = item.getItemMeta().displayName();
                if (name != null && name.equals(move.move.getName()))
                    return move;
            }
        }
        return null;
    }
}
