package me.chimkenu.mangax.utils;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.List;

public class PlayerDataUtil {
    public static List<Moves> getMoves(Player player, Characters character) {
        List<String> moves = getConfig().getStringList("player-data." + player.getUniqueId() + ".move-set" + character);
        return moves.stream().map(Moves::valueOf).toList();
    }

    public static void setMoves(Player player, Characters character, List<Moves> moves) {
        List<String> strings = moves.stream().map(Enum::toString).toList();
        getConfig().set("player-data." + player.getUniqueId() + ".move-set" + character, strings);
        saveConfig();
    }

    public static FileConfiguration getConfig() {
        return MangaX.getPlugin(MangaX.class).getConfig();
    }

    private static void saveConfig() {
        MangaX.getPlugin(MangaX.class).saveConfig();
    }
}
