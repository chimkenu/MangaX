package me.chimkenu.mangax.enums;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.NullMove;
import me.chimkenu.mangax.characters.deku.*;
import me.chimkenu.mangax.characters.diavolo.*;
import me.chimkenu.mangax.characters.gojo.*;
import me.chimkenu.mangax.characters.goku.*;
import me.chimkenu.mangax.characters.jotaro.*;
import me.chimkenu.mangax.characters.naruto.*;
import me.chimkenu.mangax.characters.phoenix.*;
import me.chimkenu.mangax.characters.tanjiro.*;
import me.chimkenu.mangax.characters.todoroki.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum Moves {
    NULL (new NullMove()), // This is for empty slots in load out customization
    JOTARO_STAND_BARRAGE (new StandBarrage()),
    JOTARO_HEAVY_HIT (new HeavyHit()),
    JOTARO_STAND_JUMP (new StandJump()),
    JOTARO_ZA_WARUDO (new TheWorld()),
    JOTARO_BALL_BEARING (new BallBearing()),
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
    NARUTO_DODGE (new Dodge()),
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
    TODOROKI_ICE_PATH (new IcePath()),
    PHOENIX_TAKE_THAT (new TakeThat()),
    PHOENIX_HOLD_IT (new HoldIt()),
    PHOENIX_OBJECTION (new Objection()),
    PHOENIX_SPIRIT_DEFENSE (new SpiritDefense()),
    GOJO_BACKHAND (new Backhand()),
    GOJO_RED_REVERSAL (new RedReversal()),
    GOJO_COLLAPSING_BLUE (new CollapsingBlue()),
    GOJO_REVERSE_CURSED_TECHNIQUE (new ReverseCursedTechnique()),
    GOJO_NAH_ID_WIN (new NahIdWin()),
    GOJO_INFINITY (new Infinity());

    public final Move move;

    Moves(Move move) {
        this.move = move;
    }

    public static Moves getMoveFromItem(ItemStack item) {
        for (Moves move : Moves.values()) {
            if (move == Moves.NULL) continue;
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.getPersistentDataContainer().has(move.move.getKey())) return move;
        }
        return null;
    }
}
