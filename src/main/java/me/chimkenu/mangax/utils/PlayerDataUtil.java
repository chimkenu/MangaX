package me.chimkenu.mangax.utils;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class PlayerDataUtil {
    public static List<Moves> getMoves(Player player, Characters character) {
        fixData(player, character);
        List<String> moves = getConfig().getStringList("player-data." + player.getUniqueId() + ".move-set" + character);
        return moves.stream().map(Moves::valueOf).toList();
    }

    public static void setMoves(Player player, Characters character, List<Moves> moves) {
        List<String> strings = moves.stream().map(Enum::toString).toList();
        getConfig().set("player-data." + player.getUniqueId() + ".move-set" + character, strings);
        saveConfig();
    }

    private static void fixData(Player player, Characters character) {
        List<String> moves = getConfig().getStringList("player-data." + player.getUniqueId() + ".move-set" + character);
        if (moves.size() != 9) {
            Iterator<Moves> movesIterator = Arrays.stream(Moves.values()).filter(m -> m.toString().contains(character.toString())).iterator();
            for (int i = 0; i < 4; i++) {
                moves.add(movesIterator.next().toString());
            }
            for (int i = 0; i < 5; i++) {
                moves.add(Moves.NULL.toString());
            }
            getConfig().set("player-data." + player.getUniqueId() + ".move-set" + character, moves);
            saveConfig();
        }
    }

    public static FileConfiguration getConfig() {
        return MangaX.getPlugin(MangaX.class).getConfig();
    }

    private static void saveConfig() {
        MangaX.getPlugin(MangaX.class).saveConfig();
    }
}
