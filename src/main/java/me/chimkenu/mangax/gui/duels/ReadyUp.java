package me.chimkenu.mangax.gui.duels;

import me.chimkenu.mangax.gui.GUI;
import me.chimkenu.mangax.utils.SkullUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class ReadyUp extends GUI {
    private boolean isActive;

    public ReadyUp(JavaPlugin plugin, List<Player> team1, List<Player> team2) {
        super(9 * Math.max(team1.size(), team2.size()), Component.text("Duels"));
        int size = getInventory().getSize();

        ItemStack divider = newGUIItem(Material.BLACK_STAINED_GLASS_PANE, Component.text(""));

        for (int i = 0; i < size / 9; i++) {
            if (i < team1.size()) {
                setItem(i + 1, SkullUtil.getSkull(team1.get(i).getUniqueId()));
                setItem(i + 2, newGUIItem(Material.GRAY_CONCRETE, Component.text("Waiting...")));
            }
            setItem(i + 4, divider, ignore -> {});
            if (i < team2.size()) {
                setItem(i + 7, SkullUtil.getSkull(team2.get(i).getUniqueId()));
                setItem(i + 6, newGUIItem(Material.GRAY_CONCRETE, Component.text("Waiting...")));
            }
        }
    }

    @Override
    public boolean onClose(Player player) {
        return !isActive;
    }
}
