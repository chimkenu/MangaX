package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.gui.personal.CharacterSelection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KitCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Only players may execute this command.", NamedTextColor.RED));
            return true;
        }

        CharacterSelection characterSelection = new CharacterSelection(player, 0);
        characterSelection.open(player);
        return true;
    }
}
