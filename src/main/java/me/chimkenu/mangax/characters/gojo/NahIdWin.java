package me.chimkenu.mangax.characters.gojo;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class NahIdWin extends Move {
    public NahIdWin() {
        super(null, null, 0, 40, Material.IRON_LEGGINGS, Component.text("NAH, I'D WIN.").decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            Bukkit.broadcast(entity.name()
                    .append(text(": "))
                    .append(getName()));

            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.damage(entity.getHealth(), entity);
                }
            }.runTaskLater(plugin, 20);
        };
    }

    @Override
    public @NotNull String[] getLore() {
        return new String[0];
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.HIGH, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.NONE, MoveInfo.Type.SINGLE, MoveInfo.Difficulty.TRIVIAL, 1, 1, 1, false);
    }
}
