package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.games.GameManager;
import me.chimkenu.mangax.games.Lobby;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static net.kyori.adventure.text.Component.text;

public class SpectateCommand implements CommandExecutor {
    private final Lobby lobby;

    public SpectateCommand(Lobby lobby) {
        this.lobby = lobby;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Only players can execute this command.", NamedTextColor.RED));
            return true;
        }

        if (player.getWorld() != lobby.getLobbySpawn().getWorld()) {
            sender.sendMessage(text("You have to be at spawn to run this command!", NamedTextColor.RED));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(text().content("Missing arguments. ").color(NamedTextColor.RED)
                    .append(text("/spectate <player>", NamedTextColor.GRAY))
                    .build());
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(text("Couldn't find that player. Did you type it correctly?", NamedTextColor.RED));
            return true;
        }

        for (GameManager game : lobby.getGames().values()) {
            if (game != null && game.isRunning() && game.getPlayers().containsPlayer(target)) {
                lobby.setSpectator(player);
                player.teleport(target);
                return true;
            }
        }

        sender.sendMessage(text("This player is not in a game."));
        return true;
    }
}
