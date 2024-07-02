package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.listeners.TruceListener;
import me.chimkenu.mangax.listeners.TruceListener.Truce;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class TruceCommand implements CommandExecutor {
    private final long REQUEST_DURATION = 1000 * 30;
    private final TruceListener truceListener;
    private final ArrayList<Request> requests;

    public TruceCommand(TruceListener truceListener) {
        this.truceListener = truceListener;
        requests = new ArrayList<>();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        clearExpiredRequests();

        if (!(sender instanceof Player player)) {
            // allow console to truce two entities
            if (args.length == 2) {
                LivingEntity one;
                LivingEntity two;
                try {
                    one = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[0]));
                    two = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
                    if (one == null || two == null || one == two) throw new IllegalArgumentException();
                } catch (IllegalArgumentException | ClassCastException ignored) {
                    sender.sendMessage(Component.text("Something went wrong with adding these entities. Make sure the arguments are UUIDs of two valid and distinct LivingEntities.", NamedTextColor.RED));
                    return true;
                }

                if (truceListener.containsTruce(one, two)) {
                    truceListener.removeTruce(one, two);
                    sender.sendMessage(Component.text("Removed the truce from these two.", NamedTextColor.GREEN));
                } else {
                    truceListener.addTruce(one, two);
                    sender.sendMessage(Component.text("Added these two to a truce.", NamedTextColor.GREEN));
                }
                return true;
            }

            sender.sendMessage(Component.text("Only players may use this command.", NamedTextColor.RED));
            return true;
        }

        // player truces
        if (args.length == 0) {
            sender.sendMessage(Component.text("Missing arguments.", NamedTextColor.RED)
                    .append(Component.text(" Usage: /truce [add|remove|list] [player]", NamedTextColor.GRAY)));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Please specify a player to form a truce.", NamedTextColor.RED)
                            .append(Component.text(player.isOp() ? " (or a valid UUID!)" : "", NamedTextColor.DARK_GRAY)));
                    return true;
                }

                LivingEntity target = Bukkit.getPlayer(args[1]);
                if (target == null && player.isOp()) {
                    try {
                        target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
                    } catch (IllegalArgumentException | ClassCastException ignored) {}
                }

                if (target == null) {
                    sender.sendMessage(Component.text(args[1], NamedTextColor.YELLOW)
                            .append(Component.text(" could not be found. Did you type it correctly?", NamedTextColor.RED)));
                    return true;
                } else if (target == player) {
                    sender.sendMessage(Component.text("You can't form a truce with yourself, silly!", NamedTextColor.YELLOW));
                    return true;
                } else if (!(target instanceof Player)) {
                    truceListener.addTruce(player, target);
                    sender.sendMessage(Component.text("Formed a truce.", NamedTextColor.GREEN));
                    return true;
                }

                if (truceListener.containsTruce(player, target)) {
                    sender.sendMessage(Component.text("You already have a truce with them.", NamedTextColor.YELLOW));
                    return true;
                }

                Truce truce = new Truce(player, target);
                for (Request request : requests) {
                    if (request.truce().equals(truce)) {
                        if (request.truce().one() == player) {
                            sender.sendMessage(Component.text("Please wait for your request to be accepted.", NamedTextColor.RED));
                        } else {
                            truceListener.addTruce(player, target);
                            sender.sendMessage(Component.text("Accepted the truce request. You have now formed a truce with ", NamedTextColor.YELLOW)
                                    .append(target.name().color(NamedTextColor.GOLD)));
                            target.sendMessage(player.displayName()
                                    .append(Component.text(" has accepted your truce request. You have now formed a truce with ", NamedTextColor.YELLOW))
                                    .append(player.displayName().color(NamedTextColor.GOLD)));
                        }

                        return true;
                    }
                }

                requests.add(new Request(truce, System.currentTimeMillis()));
                sender.sendMessage(Component.text("You have requested to form a truce with ")
                        .append(target.name())
                        .append(Component.text(". They have 30 seconds to respond.")));
                target.sendMessage(player.displayName()
                        .append(Component.text(" has requested to form a truce with you. You have 30 seconds to accept by doing ", NamedTextColor.YELLOW))
                        .append(Component.text("/truce add ", NamedTextColor.GOLD))
                        .append(player.name().color(NamedTextColor.GOLD)));
            }

            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(Component.text("Please specify a player to remove.", NamedTextColor.RED)
                            .append(Component.text(player.isOp() ? " (or a valid UUID!)" : "", NamedTextColor.DARK_GRAY)));
                    return true;
                }

                LivingEntity target = Bukkit.getPlayer(args[1]);
                if (target == null && player.isOp()) {
                    try {
                        target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
                    } catch (IllegalArgumentException | ClassCastException ignored) {}
                }

                if (target == null) {
                    sender.sendMessage(Component.text(args[1], NamedTextColor.YELLOW)
                            .append(Component.text(" could not be found. Did you type it correctly?", NamedTextColor.RED)));
                    return true;
                } else if (target == player) {
                    sender.sendMessage(Component.text("You can't remove yourself, silly!", NamedTextColor.YELLOW));
                    return true;
                } else if (!(target instanceof Player)) {
                    truceListener.removeTruce(player, target);
                    sender.sendMessage(Component.text("Removed the truce (if there was one).", NamedTextColor.GREEN));
                    return true;
                }

                if (truceListener.containsTruce(player, target)) {
                    truceListener.removeTruce(player, target);
                    sender.sendMessage(Component.text("You no longer have a truce with ", NamedTextColor.YELLOW)
                            .append(target.name().color(NamedTextColor.GOLD)));
                    target.sendMessage(Component.text("You no longer have a truce with ", NamedTextColor.YELLOW)
                            .append(player.displayName().color(NamedTextColor.GOLD)));
                } else {
                    sender.sendMessage(Component.text("You don't have an existing truce with ", NamedTextColor.RED)
                            .append(target.name().color(NamedTextColor.GOLD)));
                }

            }

            case "list" -> {
                player.sendMessage(Component.text("You have a truce with the following:", NamedTextColor.YELLOW));
                truceListener.getTruceList(player).forEach(truce -> {
                    player.sendMessage(Component.text("  ")
                            .append(truce.one() == player ? truce.two().name() : truce.one().name()));
                });
            }

            default -> sender.sendMessage(Component.text("Unknown arguments.", NamedTextColor.RED)
                    .append(Component.text(" Usage: /truce [add|remove|list] [player]", NamedTextColor.GRAY)));
        }

        return true;
    }

    private void clearExpiredRequests() {
        requests.removeIf(request -> System.currentTimeMillis() - request.timeRequested > REQUEST_DURATION);
    };

    private record Request(Truce truce, long timeRequested) {}
}
