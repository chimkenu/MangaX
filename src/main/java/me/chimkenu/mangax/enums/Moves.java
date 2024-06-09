package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.deku.*;
import me.chimkenu.mangax.characters.diavolo.*;
import me.chimkenu.mangax.characters.goku.*;
import me.chimkenu.mangax.characters.jotaro.*;
import me.chimkenu.mangax.characters.naruto.*;
import me.chimkenu.mangax.characters.tanjiro.*;
import me.chimkenu.mangax.characters.todoroki.*;
import org.bukkit.inventory.ItemStack;

public enum Moves {
    JOTARO_STAND_BARRAGE (new StandBarrage()),
    JOTARO_HEAVY_HIT (new HeavyHit()),
    JOTARO_STAND_JUMP (new StandJump()),
    JOTARO_ZA_WARUDO (new TheWorld()),
    TANJIRO_DROP_RIPPLE_THRUST (new DropRippleThrust()),
    TANJIRO_WATER_WHEEL (new WaterWheel()),
    TANJIRO_STRIKING_TIDE (new StrikingTide()),
    TANJIRO_FAKE_RAINBOW (new FakeRainbow()),
    GOKU_DRAGON_FIST (new DragonFist()),
    GOKU_DASH (new Dash()),
    GOKU_KAIO_KEN_10 (new KaioKen()),
    GOKU_KAMEHAMEHA (new Kamehameha()),
    NARUTO_RASENSHURIKEN (new Rasenshuriken()),
    NARUTO_MULTI_SHADOW_CLONE_JUTSU (new MultiShadowCloneJutsu()),
    NARUTO_DODGE (new Dodge(null)),
    NARUTO_RASENGAN (new Rasengan()),
    DEKU_DETROIT_SMASH (new DetroitSmash()),
    DEKU_DELAWARE_SMASH (new DelawareSmash()),
    DEKU_SHOOT_STYLE_LEAP (new ShootStyleLeap()),
    DEKU_FULL_BLAST (new FullBlast()),
    DIAVOLO_CRIMSON_BARRAGE (new CrimsonBarrage()),
    DIAVOLO_IMPALE (new Impale()),
    DIAVOLO_TIME_SKIP (new TimeSkip()),
    DIAVOLO_EPITAPH (new Epitaph()),
    TODOROKI_FLAMETHROWER (new Flamethrower()),
    TODOROKI_FLASHFIRE_FIST (new FlashfireFist()),
    TODOROKI_ICE_WALL (new IceWall()),
    TODOROKI_ICE_PATH (new IcePath());

    public final Move move;

    Moves(Move move) {
        this.move = move;
    }

    public static Moves getMoveFromItem(ItemStack item) {
        for (Moves move : Moves.values()) {
            ItemStack moveItem = move.move.getItem();
            if (moveItem.getType().equals(item.getType()) && moveItem.displayName().equals(item.displayName())) {
                return move;
            }
        }
        return null;
    }
}
