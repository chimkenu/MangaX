package me.chimkenu.mangax.commands;

import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.ai.goals.ReactiveTargetGoal;
import me.chimkenu.mangax.enums.Moves;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Summon implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if (sender instanceof Player player) {
            if (args.length == 0) {
                player.sendMessage(Component.text("put the name of a character like /sommun JOTARO"));
                return true;
            }

            Set<Moves> moveSet = Arrays.stream(Moves.values()).filter(m -> m.toString().contains(args[0])).collect(Collectors.toSet());

            Husk mob = player.getWorld().spawn(player.getLocation(), Husk.class);
            Bukkit.getMobGoals().removeAllGoals(mob);
            Bukkit.getMobGoals().addGoal(mob, 0, new ReactiveTargetGoal(MangaX.getPlugin(MangaX.class), mob, moveSet));
            mob.setTarget(player);
        }
        return true;
    }
}
