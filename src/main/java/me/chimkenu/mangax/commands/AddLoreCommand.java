package me.chimkenu.mangax.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddLoreCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("put something");
            return true;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            sender.sendMessage("hold something");
            return true;
        }

        List<Component> components = meta.lore();
        if (components == null)
            components = new ArrayList<>();

        StringBuilder lore = new StringBuilder();
        for (String s : args) {
            lore.append(s).append(" ");
        }

        components.add(MiniMessage.miniMessage().deserialize(lore.toString()));

        meta.lore(components);
        item.setItemMeta(meta);

        sender.sendMessage("there");

        return true;
    }
}
