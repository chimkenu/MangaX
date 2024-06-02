package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;

import java.util.ArrayList;

public class DelawareSmash extends Move {
    public DelawareSmash() {
        super((plugin, player) -> {
            player.sendMessage(Component.text("hello im working on this"));
        }, null, 15 * 20, Material.RAW_GOLD, Component.text("Delaware Smash").color(TextColor.fromHexString("#106761")), new ArrayList<>());
    }
}
