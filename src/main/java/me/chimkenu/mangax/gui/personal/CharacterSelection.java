package me.chimkenu.mangax.gui.personal;

import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.gui.GUI;
import me.chimkenu.mangax.utils.PlayerDataUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static me.chimkenu.mangax.utils.SkullUtil.getSkull;
import static me.chimkenu.mangax.utils.PlayerDataUtil.*;

public class CharacterSelection extends GUI {
    private final Player player;
    private Characters character;

    public CharacterSelection(Player player, int page) {
        super(45, Component.text("Character Selection").color(NamedTextColor.BLACK));
        this.player = player;
        this.character = getActiveCharacter();

        ItemStack divider = newGUIItem(Material.GRAY_STAINED_GLASS_PANE, Component.text(""));

        // dividers
        for (int i = 0; i < 9; i++) {
            setItem(9 + i, divider, player1 -> {});
            setItem(27 + i, divider, player1 -> {});
        }

        updateCharacterPage(page);
        updateCharacterSelected(character);
    }

    @Override
    public boolean onClose(Player player) {
        List<Moves> moves = getCurrentlySelectedMoves();

        for (int i = 0; i < 9; i++) {
            player.getInventory().setItem(i, moves.get(i).move.getItem());
        }

        // save to config
        PlayerDataUtil.setMoves(player, character, moves);
        return true;
    }

    private void updateCharacterPage(int newPage) {
        final int CHARACTERS_PER_PAGE = 7;
        final ItemStack nextPage = newGUIItem(Material.ARROW, Component.text("Next page").color(NamedTextColor.GRAY));
        final ItemStack prevPage = newGUIItem(Material.ARROW, Component.text("Previous page").color(NamedTextColor.GRAY));

        // character list
        Characters[] characters = Characters.values();
        int k = 0;
        while (newPage * CHARACTERS_PER_PAGE + k < characters.length && k < CHARACTERS_PER_PAGE) {
            Characters selected = characters[newPage * CHARACTERS_PER_PAGE + k];
            setItem(k + 1,
                    modifyItem(getSkull(PlayerDataUtil.getConfig().getString("character-skins." + characters[newPage * CHARACTERS_PER_PAGE + k].toString().toLowerCase())),
                            Component.text(selected.toString(), NamedTextColor.WHITE, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false),
                            false),
                    player1 -> updateCharacterSelected(selected));
            k++;
        }
        for (; k < CHARACTERS_PER_PAGE; k++) {
            setItem(k + 1, newGUIItem(Material.AIR, Component.text("")));
        }

        // page arrows
        if (newPage > 0) {
            setItem(0, prevPage, player1 -> updateCharacterPage(newPage - 1));
        } else {
            setItem(0, newGUIItem(Material.AIR, Component.text("")));
        }
        if (characters.length > (newPage + 1) * CHARACTERS_PER_PAGE) {
            setItem(8, nextPage, player1 -> updateCharacterPage(newPage + 1));
        } else {
            setItem(8, newGUIItem(Material.AIR, Component.text("")));
        }
    }

    private void updateCharacterSelected(Characters newCharacter) {
        character = newCharacter;
        List<Moves> moves = new ArrayList<>(getMoves(player, character));

        // just make sure it's exactly 9 items
        while (moves.size() != 9) {
            if (moves.size() > 9) {
                moves.removeLast();
            } else {
                moves.add(Moves.NULL);
            }
        }

        // show character load out
        for (int i = 0; i < 9; i++) {
            setItem(i + 36, moves.get(i).move.getItem(), new Action() {
                @Override
                public void click(Player player) {
                    // ignore click
                }

                @Override
                public boolean isFixed(InventoryAction action) {
                    int count = getNumberOfCurrentlySelectedMoves();
                    if (action.toString().contains("PLACE")) count++;
                    else if (action.toString().contains("PICKUP")) count--;
                    return count > 4;
                }
            });
        }

        // display the rest of the moves that weren't picked
        AtomicInteger j = new AtomicInteger();
        Arrays.stream(Moves.values()).filter(m -> m.toString().contains(newCharacter.toString())).forEach(m -> {
            if (!moves.contains(m)) {
                setItem(j.get() + 18, m.move.getItem(), new Action() {
                    @Override
                    public void click(Player player) {
                        // ignore click
                    }

                    @Override
                    public boolean isFixed(InventoryAction action) {
                        return false;
                    }
                });
                j.getAndIncrement();
            }
        });
        for (; j.get() < 9; j.getAndIncrement()) {
            setItem(j.get() + 18, newGUIItem(Material.AIR, Component.text("")));
        }
    }

    private List<Moves> getCurrentlySelectedMoves() {
        List<Moves> moves = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            ItemStack item = getInventory().getItem(i + 36);
            Moves move = item == null ? null : Moves.getMoveFromItem(item);
            if (move == null) {
                move = Moves.NULL;
            }
            moves.add(move);
        }
        return moves;
    }

    private int getNumberOfCurrentlySelectedMoves() {
        return (int) getCurrentlySelectedMoves().stream().filter(m -> m != Moves.NULL).count();
    }

    private Characters getActiveCharacter() {
        Characters character = null;
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null) {
                Characters check = Characters.getCharacterFromItem(item);
                if (check != null) {
                    character = check;
                }
            }
        }
        return character != null ? character : Characters.JOTARO;
    }
}
