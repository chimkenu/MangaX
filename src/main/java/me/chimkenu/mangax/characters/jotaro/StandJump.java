package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class StandJump extends Move {
    public StandJump() {
        super((plugin, entity) -> entity.setVelocity((new Vector(entity.getVelocity().getX(), 2, entity.getVelocity().getZ())).add(entity.getLocation().getDirection().multiply(0.2))), null, 0, 12 * 20, Material.MAGENTA_GLAZED_TERRACOTTA, Component.text("Stand Jump").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.VERTICAL, MoveInfo.Type.MANOEUVRE, MoveInfo.Difficulty.TYPICAL, 10, 1, 1, false);
    }
}
