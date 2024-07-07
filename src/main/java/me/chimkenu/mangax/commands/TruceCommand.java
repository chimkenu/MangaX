package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.listeners.TruceListener;
import me.chimkenu.mangax.listeners.TruceListener.Truce;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.kyori.adventure.text.Component.text;

public class TruceCommand implements CommandExecutor, TabCompleter {
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
                    sender.sendMessage(text().content("Something went wrong with adding these entities. Make sure the arguments are UUIDs of two valid and distinct LivingEntities.").color(NamedTextColor.RED).build());
                    return true;
                }

                if (truceListener.containsTruce(one, two)) {
                    truceListener.removeTruce(one, two);
                    sender.sendMessage(text().content("Removed the truce from these two.").color(NamedTextColor.GREEN).build());
                } else {
                    truceListener.addTruce(one, two);
                    sender.sendMessage(text().content("Added these two to a truce.").color(NamedTextColor.GREEN).build());
                }
                return true;
            }

            sender.sendMessage(text().content("Only players may use this command.").color(NamedTextColor.RED).build());
            return true;
        }

        // player truces
        if (args.length == 0) {
            sender.sendMessage(text().content("Missing arguments.").color(NamedTextColor.RED)
                    .append(text(" Usage: /truce [add|remove|list] [player]", NamedTextColor.GRAY))
                    .build());
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "add" -> {
                if (args.length < 2) {
                    sender.sendMessage(text().content("Please specify a player to form a truce.").color(NamedTextColor.RED)
                            .append(text(player.isOp() ? " (or a valid UUID!)" : "", NamedTextColor.DARK_GRAY))
                            .build());
                    return true;
                }

                LivingEntity target = Bukkit.getPlayer(args[1]);
                if (target == null && player.isOp()) {
                    try {
                        target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
                    } catch (IllegalArgumentException | ClassCastException ignored) {}
                }

                if (target == null) {
                    sender.sendMessage(text().content(args[1]).color(NamedTextColor.YELLOW)
                            .append(text(" could not be found. Did you type it correctly?", NamedTextColor.RED))
                            .build());
                    return true;
                } else if (target == player) {
                    sender.sendMessage(text().content("You can't form a truce with yourself, silly!").color(NamedTextColor.YELLOW).build());
                    return true;
                } else if (!(target instanceof Player)) {
                    truceListener.addTruce(player, target);
                    sender.sendMessage(text().content("Formed a truce.").color(NamedTextColor.GREEN).build());
                    return true;
                }

                if (truceListener.containsTruce(player, target)) {
                    sender.sendMessage(text().content("You already have a truce with them.").color(NamedTextColor.YELLOW).build());
                    return true;
                }

                Truce truce = new Truce(player, target);
                for (Request request : requests) {
                    if (request.truce().equals(truce)) {
                        if (request.truce().one() == player) {
                            sender.sendMessage(text().content("Please wait for your request to be accepted.").color(NamedTextColor.RED).build());
                        } else {
                            truceListener.addTruce(player, target);
                            sender.sendMessage(text().content("Accepted the truce request. You have now formed a truce with ").color(NamedTextColor.YELLOW)
                                    .append(target.name().color(NamedTextColor.GOLD))
                                    .build());
                            target.sendMessage(player.displayName().color(NamedTextColor.GOLD)
                                    .append(Component.text(" has accepted your truce request. You have now formed a truce with ", NamedTextColor.YELLOW))
                                    .append(player.displayName().color(NamedTextColor.GOLD)));
                        }

                        return true;
                    }
                }

                requests.add(new Request(truce, System.currentTimeMillis()));
                sender.sendMessage(text().content("You have requested to form a truce with ").color(NamedTextColor.YELLOW)
                        .append(target.name().color(NamedTextColor.GOLD))
                        .append(text(". They have 30 seconds to respond.", NamedTextColor.YELLOW))
                        .build());
                target.sendMessage(player.displayName().color(NamedTextColor.GOLD)
                        .append(text(" has requested to form a truce with you. You have 30 seconds to accept by typing ", NamedTextColor.YELLOW))
                        .append(text("/truce add ", NamedTextColor.GOLD))
                        .append(player.name().color(NamedTextColor.GOLD)));
            }

            case "remove" -> {
                if (args.length < 2) {
                    sender.sendMessage(text().content("Please specify a player to remove.").color(NamedTextColor.RED)
                            .append(Component.text(player.isOp() ? " (or a valid UUID!)" : "", NamedTextColor.DARK_GRAY))
                            .build());
                    return true;
                }

                LivingEntity target = Bukkit.getPlayer(args[1]);
                if (target == null && player.isOp()) {
                    try {
                        target = (LivingEntity) Bukkit.getEntity(UUID.fromString(args[1]));
                    } catch (IllegalArgumentException | ClassCastException ignored) {}
                }

                if (target == null) {
                    sender.sendMessage(text().content(args[1]).color(NamedTextColor.GOLD)
                            .append(text(" could not be found. Did you type it correctly?", NamedTextColor.RED))
                            .build());
                    return true;
                } else if (target == player) {
                    sender.sendMessage(text().content("You can't remove yourself, silly!").color(NamedTextColor.YELLOW));
                    return true;
                } else if (!(target instanceof Player)) {
                    truceListener.removeTruce(player, target);
                    sender.sendMessage(text().content("Removed the truce (if there was one).").color(NamedTextColor.GREEN).build());
                    return true;
                }

                if (truceListener.containsTruce(player, target)) {
                    truceListener.removeTruce(player, target);
                    sender.sendMessage(text().content("You no longer have a truce with ").color(NamedTextColor.YELLOW)
                            .append(target.name().color(NamedTextColor.GOLD))
                            .build());
                    target.sendMessage(text().content("You no longer have a truce with ").color(NamedTextColor.YELLOW)
                            .append(player.displayName().color(NamedTextColor.GOLD))
                            .build());
                } else {
                    sender.sendMessage(text().content("You don't have an existing truce with ").color(NamedTextColor.RED)
                            .append(target.name().color(NamedTextColor.GOLD))
                            .build());
                }

            }

            case "list" -> {
                player.sendMessage(Component.text("You have a truce with the following:", NamedTextColor.YELLOW));
                truceListener.getTruceList(player).forEach(truce -> player.sendMessage(
                        text().content("\n  ")
                                .append(truce.one() == player ? truce.two().name().color(NamedTextColor.GOLD) : truce.one().name().color(NamedTextColor.GOLD))
                                .build()));
            }

            default -> sender.sendMessage(text().content("Unknown arguments.").color(NamedTextColor.RED)
                    .append(text(" Usage: /truce [add|remove|list] [player]", NamedTextColor.GRAY))
                    .build());
        }

        return true;
    }

    private void clearExpiredRequests() {
        requests.removeIf(request -> System.currentTimeMillis() - request.timeRequested > REQUEST_DURATION);
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return List.of("add", "remove", "list");
            }
            case 2 -> {
                return null;
            }
            default -> {
                return List.of();
            }
        }
    }

    private record Request(Truce truce, long timeRequested) {}
}
