package me.chimkenu.mangax.characters.jotaro;

import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.enums.MoveInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class StandJump extends Move {
    public StandJump() {
        super(null, null, 0, 12 * 20, Material.MAGENTA_GLAZED_TERRACOTTA, Component.text("Stand Jump").color(NamedTextColor.DARK_PURPLE).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));

        this.activate = (plugin, entity) -> {
            jump(entity);
            new BukkitRunnable() {
                @Override
                public void run() {
                    jump(entity);
                }
            }.runTaskLater(plugin, 2);
        };
    }

    private void jump(LivingEntity entity) {
        Vector v = entity.getVelocity();
        v.setY(1.5);
        entity.setVelocity(v);
    }

    @Override
    public String[] getLore() {
        return new String[] {};
    }

    @Override
    public @NotNull MoveInfo getMoveInfo() {
        return new MoveInfo(MoveInfo.Damage.NONE, MoveInfo.Range.SELF, MoveInfo.Knockback.NONE, MoveInfo.Manoeuvre.VERTICAL, MoveInfo.Type.MANOEUVRE, MoveInfo.Difficulty.TYPICAL, 10, 1, 1, false);
    }
}
