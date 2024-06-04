package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.enums.Moves;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GetItem implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        for (Moves m : Moves.values()) {
            player.getInventory().addItem(m.move.getItem());
        }
        return true;
    }
}
