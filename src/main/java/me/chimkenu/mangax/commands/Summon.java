package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.ai.goals.ReactiveTargetGoal;
import me.chimkenu.mangax.enums.Characters;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.gui.duels.MatchUp;
import me.chimkenu.mangax.gui.personal.CharacterSelection;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

public class Summon implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if (sender instanceof Player player) {
            if (args.length == 2) {
                CharacterSelection characterSelection = new CharacterSelection(player, 0);
                characterSelection.open(player);
                return true;
            } else if (args.length == 3) {
                MatchUp matchUp = new MatchUp(MangaX.getPlugin(MangaX.class), List.of(player), List.of(player), 1);
                matchUp.open(player);
                return true;
            }

            Husk mob = player.getWorld().spawn(player.getLocation(), Husk.class);
            Bukkit.getMobGoals().removeAllGoals(mob);
            Bukkit.getMobGoals().addGoal(mob, 0, new ReactiveTargetGoal(MangaX.getPlugin(MangaX.class), mob, Set.of(Moves.JOTARO_STAND_BARRAGE, Moves.JOTARO_HEAVY_HIT, Moves.JOTARO_STAND_JUMP, Moves.JOTARO_ZA_WARUDO)));
            mob.setTarget(player);
        }
        return true;
    }
}
