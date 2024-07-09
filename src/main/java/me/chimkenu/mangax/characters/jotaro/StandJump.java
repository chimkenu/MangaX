package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StandJump extends Move {
    public StandJump() {
        super((plugin, entity) -> entity.setVelocity((new Vector(entity.getVelocity().getX(), 2, entity.getVelocity().getZ())).add(entity.getLocation().getDirection().multiply(0.2))), null, 0, 12 * 20, Material.MAGENTA_GLAZED_TERRACOTTA, Component.text("Stand Jump").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {
            "<#c4c4c4>Uses Star Platinum's strength</#c4c4c4>",
            "<#c4c4c4>to push off the ground, leaping into</#c4c4c4>",
            "<#c4c4c4>the air to cover extraordinary heights.</#c4c4c4>",
            "<reset>",
            "<i:false><#ffffff>Type:</#ffffff> <u>Movement</u>",
            "<i:false><#ffffff>Distance:</#ffffff> <#e8eb46>Mid</#e8eb46>",
            "<i:false><#ffffff>Cooldown:</#ffffff> <#e6dd6c><b>12</b> seconds</#e6dd6c>",
            "<reset>",
            "<i:false><#f6ff52><b>[<key:key.attack>]</b></#f6ff52> <#ffffff>to use ability</#ffffff>"
        };
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.VERTICAL, MoveInfo.Type.MANOEUVRE, MoveInfo.Difficulty.TYPICAL, 10, 1, 1, false);
    }
}
