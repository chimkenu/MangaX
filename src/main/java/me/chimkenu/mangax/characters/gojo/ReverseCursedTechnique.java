package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

public class ReverseCursedTechnique extends Move {
    public ReverseCursedTechnique() {
        super(null, null, 0, 10, Material.WHITE_STAINED_GLASS, Component.text("Reverse Cursed Technique"));
    }

    @Override
    public @NotNull String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return null;
    }
}
