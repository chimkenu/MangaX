package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.enums.WorldData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static net.kyori.adventure.text.Component.text;

public class WorldDataCommand implements CommandExecutor, TabCompleter {
    private final JavaPlugin plugin;

    public WorldDataCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.isOp()) {
            sender.sendMessage(text("Insufficient permissions.", NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(text("Only players can use this command.", NamedTextColor.RED));
            return true;
        }

        World world = player.getWorld();
        if (!world.getName().contains(plugin.getConfig().getString("map-prefix", "mangax"))) {
            sender.sendMessage(text("You're not in a valid world.", NamedTextColor.RED));
            return true;
        }

        PersistentDataContainer pdc = world.getPersistentDataContainer();

        if (args.length < 2) {
            sender.sendMessage(text()
                    .content("Missing arguments. ").color(NamedTextColor.RED)
                    .append(text("/worlddata <data> <get|set> [value]", NamedTextColor.GRAY))
                    .build());
            return true;
        }

        WorldData data;
        try {
            data = WorldData.valueOf(args[0]);
        } catch (IllegalArgumentException ignored) {
            sender.sendMessage(text("Unknown data type, did you type it correctly?", NamedTextColor.RED));
            return true;
        }

        String arg = args[1].toLowerCase();
        switch (arg) {
            case "get" -> {
                String result = pdc.get(data.getKey(), PersistentDataType.STRING);
                sender.sendMessage(text()
                        .content(data + ": ").color(NamedTextColor.YELLOW)
                        .append(Component.text(result == null ? "null" : result))
                        .build());
            }

            case "set" -> {
                StringBuilder input = new StringBuilder();
                for (int i = 2; i < args.length; i++) {
                    input.append(args[i]).append(" ");
                }
                input.deleteCharAt(input.length() - 1);

                try {
                    data.data.toData(input.toString());
                } catch (Exception ignored) {
                    sender.sendMessage(text()
                            .content("Your input could not be serialized. Please check if it was formatted correctly.")
                            .color(NamedTextColor.RED)
                            .build());
                    return true;
                }

                String result = pdc.get(data.getKey(), PersistentDataType.STRING);
                sender.sendMessage(text()
                        .content("Previous " + data + ": ").color(NamedTextColor.YELLOW)
                        .append(text(result == null ? "null" : result))
                        .build());

                pdc.set(data.getKey(), PersistentDataType.STRING, input.toString());
                result = pdc.get(data.getKey(), PersistentDataType.STRING);
                sender.sendMessage(text()
                        .content("New " + data + ": ").color(NamedTextColor.YELLOW)
                        .append(text(result == null ? "null" : result))
                        .build());
            }

            default -> sender.sendMessage(text("Unknown argument, did you type it correctly?", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        switch (args.length) {
            case 1 -> {
                return Arrays.stream(WorldData.values()).map(WorldData::toString).toList();
            }
            case 2 -> {
                return List.of("get", "set");
            }
            default -> {
                return List.of();
            }
        }
    }
}
