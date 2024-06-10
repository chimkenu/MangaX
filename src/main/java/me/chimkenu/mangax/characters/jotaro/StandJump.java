package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class StandJump extends Move {
    public StandJump() {
        super((plugin, entity) -> entity.setVelocity(entity.getVelocity().add(new Vector(0, 2, 0)).add(entity.getLocation().getDirection().multiply(0.2))), null, 0, 20 * 20, Material.MAGENTA_GLAZED_TERRACOTTA, Component.text("Stand Jump").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
