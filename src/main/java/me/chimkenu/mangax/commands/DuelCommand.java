package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.games.Lobby;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

import static net.kyori.adventure.text.Component.text;

public class DuelCommand implements CommandExecutor {
    private final Lobby lobby;

    public DuelCommand(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Only players can execute this command.", NamedTextColor.RED));
            return true;
        }

        if (!player.isOp()) {
            sender.sendMessage(text("Sorry! This is currently in development. Please ask an operator to start a duel for you."));
        }

        if (args.length < 2) {
            sender.sendMessage(text().content("Specify at least 2 players to duel. ").color(NamedTextColor.RED)
                    .append(text("/duel players...", NamedTextColor.GRAY))
                    .build());
            return true;
        }

        HashSet<Player> teamOne = new HashSet<>();
        HashSet<Player> teamTwo = new HashSet<>();

        int i = 0;
        for (String s : args) {
            Player target = Bukkit.getPlayer(s);
            if (target == null) {
                sender.sendMessage(text(s + " is not a valid player. Did you type it correctly?", NamedTextColor.RED));
                return true;
            }

            if (i % 2 == 0) {
                teamOne.add(target);
            } else {
                teamTwo.add(target);
            }
            i++;
        }

        sender.sendMessage(text("SENDING TO DUEL!!!!!"));
        boolean didItWork = lobby.addDuelsGame(teamOne, teamTwo);
        if (!didItWork)
            sender.sendMessage(text("it no work :("));
        return true;
    }
}
