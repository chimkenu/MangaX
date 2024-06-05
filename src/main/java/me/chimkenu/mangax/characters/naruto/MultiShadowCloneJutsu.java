package me.chimkenu.mangax.characters.naruto;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;

import java.util.ArrayList;

public class MultiShadowCloneJutsu extends Move {
    public MultiShadowCloneJutsu() {
        super((plugin, player) -> {

        }, null, 0, 20 * 20, Material.CREEPER_BANNER_PATTERN, Component.text("Multi Shadow Clone Jutsu").color(NamedTextColor.GOLD).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
