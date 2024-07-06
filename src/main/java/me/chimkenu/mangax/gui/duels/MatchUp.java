package me.chimkenu.mangax.gui.duels;

import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.gui.GUI;
import me.chimkenu.mangax.utils.PlayerDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static me.chimkenu.mangax.utils.SkullUtil.*;

public class MatchUp extends GUI {
    private final JavaPlugin plugin;
    private final List<Player> players;
    private final List<Characters> selectedCharacters;

    private final HashSet<Characters> bannedCharacters;
    private final int totalBans;

    private int page;
    private boolean isActive;
    private Characters selected;
    private int index;
    private int time;

    private final int TIME_TO_DECIDE = 15 * 20;

    public MatchUp(JavaPlugin plugin, List<Player> team1, List<Player> team2, int bansPerTeam) {
        super(18 + 9 * Math.round((team1.size() + team2.size()) / 2f), Component.text("Duels"));
        page = 0;
        this.plugin = plugin;
        bannedCharacters = new HashSet<>();
        isActive = true;
        int size = getInventory().getSize();
        totalBans = 2 * bansPerTeam;

        // arrange players
        players = new ArrayList<>();
        selectedCharacters = new ArrayList<>();
        int t1 = 0;
        int t2 = 0;
        for (int i = 0; i < team1.size() + team2.size(); i++) {
            if (i % 2 == 0) {
                players.add(t1 < team1.size() ? team1.get(t1++) : null);
            } else {
                players.add(t2 < team2.size() ? team2.get(t2++) : null);
            }
            selectedCharacters.add(null);
        }

        ItemStack divider = newGUIItem(Material.BLACK_STAINED_GLASS_PANE, Component.text(""));

        for (int i = 0; i < 9; i++) {
            setItem(size - 18 + i, divider, ignore -> {});
            if (i * 9 + 4 < size - 5) {
                setItem(i * 9 + 4, divider, ignore -> {});
            }
        }

        updateCharacterSelection();
        updateCharacterPage(0);

        next();
    }

    @Override
    public boolean onClose(Player player) {
        return !isActive;
    }

    private void updateCharacterSelection() {
        int t1 = 0;
        int t2 = 0;
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player == null) continue;

            ItemStack playerSkull = modifyItem(getSkull(player.getUniqueId()), player.displayName().decoration(TextDecoration.ITALIC, false), false);
            Characters selected = selectedCharacters.get(i);
            ItemStack characterSkull = selected == null ?
                    modifyItem(getSkull(PlayerDataUtil.getConfig().getString("character-skins.question-mark")), Component.text(""), false) :
                    modifyItem(getSkull(PlayerDataUtil.getConfig().getString("character-skins." + selected.toString().toLowerCase())), Component.text(selected.toString(), NamedTextColor.WHITE, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false), false);
            if (i % 2 == 0) {
                setItem(t1 * 9 + 1, playerSkull, ignore -> {});
                setItem(t1 * 9 + 2, characterSkull, ignore ->{});
                t1++;
            } else {
                setItem(t2 * 9 + 7, playerSkull, ignore -> {});
                setItem(t2 * 9 + 6, characterSkull, ignore ->{});
                t2++;
            }
        }
    }

    private void updateCharacterPage(int newPage) {
        final int CHARACTERS_PER_PAGE = 7;
        final int size = getInventory().getSize();
        final ItemStack nextPage = newGUIItem(Material.ARROW, Component.text("Next page").color(NamedTextColor.GRAY));
        final ItemStack prevPage = newGUIItem(Material.ARROW, Component.text("Previous page").color(NamedTextColor.GRAY));

        // character list
        Characters[] characters = Characters.values();
        int k = 0;
        while (newPage * CHARACTERS_PER_PAGE + k < characters.length && k < CHARACTERS_PER_PAGE) {
            Characters selection = characters[newPage * CHARACTERS_PER_PAGE + k];
            setItem(size - 9 + k + 1,
                modifyItem(getSkull(PlayerDataUtil.getConfig().getString("character-skins." + selection.toString().toLowerCase() + (bannedCharacters.contains(selection) ? "-locked" : ""))),
                    Component.text(selection.toString(), NamedTextColor.WHITE, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                    false),
                player1 -> {
                    if (player1 == players.get(index)) {
                        if (bannedCharacters.contains(selection)) {
                            player1.sendMessage(Component.text("This character is banned.", NamedTextColor.RED));
                            return;
                        }

                        selected = selection;
                        time = 0;
                    }
                });
            k++;
        }
        for (; k < CHARACTERS_PER_PAGE; k++) {
            setItem(size - 9 + k + 1, newGUIItem(Material.AIR, Component.text("")));
        }

        // page arrows
        if (newPage > 0) {
            setItem(size - 9, prevPage, player1 -> {
                if (player1 == players.get(index)) {
                    updateCharacterPage(newPage - 1);
                    page++;
                }
            });
        } else {
            setItem(size - 9, newGUIItem(Material.AIR, Component.text("")));
        }
        if (characters.length > (newPage + 1) * CHARACTERS_PER_PAGE) {
            setItem(size - 1, nextPage, player1 -> {
                if (player1 == players.get(index)) {
                    updateCharacterPage(newPage + 1);
                    page++;
                }
            });
        } else {
            setItem(size - 1, newGUIItem(Material.AIR, Component.text("")));
        }
    }

    private void next() {
        updateCharacterSelection();
        updateCharacterPage(page);

        // default selection to a non-banned character
        selected = Arrays.stream(Characters.values()).filter(c -> !bannedCharacters.contains(c)).toList().getFirst();

        time = TIME_TO_DECIDE;
        if (bannedCharacters.size() < totalBans) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player active = players.get(index);
                    if (time <= 0) {
                        players.forEach(p -> p.sendMessage(active.displayName()
                                .append(Component.text(" has banned "))
                                .append(Component.text(selected.toString()))));
                        bannedCharacters.add(selected);
                        do {
                            index++;
                        } while (index < players.size() && players.get(index) == null);

                        // reset index once bans are complete
                        if (bannedCharacters.size() - 1 <= totalBans) {
                            index = 0;
                        }

                        next();
                        cancel();
                        return;
                    }

                    players.forEach(p -> p.sendActionBar(active.displayName()
                            .append(Component.text( " is picking a character to ban... "))
                            .append(Component.text((time - (time % 20)) / 20))));

                    time--;
                }
            }.runTaskTimer(plugin, 1, 1);

        } else if (index < players.size()) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player active = players.get(index);
                    if (time <= 0) {
                        selectedCharacters.set(index, selected);
                        players.forEach(p -> p.sendMessage(active.displayName()
                                .append(Component.text(" selected "))
                                .append(Component.text(selected.toString()))));
                        do {
                            index++;
                        } while (index < players.size() && players.get(index) == null);
                        next();
                        cancel();
                        return;
                    }

                    for (Player player : players) {
                        player.sendActionBar(active.displayName()
                                .append(Component.text(" is choosing a character... "))
                                .append(Component.text((time - (time % 20)) / 20)));
                    }

                    time--;
                }
            }.runTaskTimer(plugin, 1, 1);

        } else {
            // start duel
            new BukkitRunnable() {
                int t = 5 * 20;
                @Override
                public void run() {
                    if (t <= 0) {
                        isActive = false;
                        players.forEach(Player::closeInventory);

                        Vector spawn1 = new Vector(987.0, 76, 999.0);
                        Vector spawn2 = new Vector(1015.0, 75.5, 999.0);
                        Vector dz = new Vector(1, 0, 0);

                        for (int i = 0; i < players.size(); i++) {
                            Player player = players.get(i);
                            if (player == null) continue;

                            List<Moves> moves = PlayerDataUtil.getMoves(player, selectedCharacters.get(i));
                            for (int j = 0; j < 9; j++) {
                                player.getInventory().setItem(j, moves.get(j).move.getItem());
                            }

                            if (i % 2 == 0) {
                                player.teleport(spawn1.toLocation(player.getWorld(), -90, 0));
                                spawn1.add(dz);
                            } else {
                                player.teleport(spawn2.toLocation(player.getWorld(), 90, 0));
                                spawn2.add(dz);
                            }
                            selectedCharacters.add(null);
                        }

                        cancel();
                        return;
                    }

                    players.forEach(p -> p.sendActionBar(Component.text("Starting in ")
                            .append(Component.text((t - (t % 20)) / 20))));

                    t--;
                }
            }.runTaskTimer(plugin, 1, 1);
        }
    }
}
