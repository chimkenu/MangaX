package me.chimkenu.mangax.commands;

import com.destroystokyo.paper.entity.ai.GoalType;
import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.ai.goals.ReactiveTargetGoal;
import me.chimkenu.mangax.characters.jotaro.StandBarrage;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.utils.ArmorStandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Summon implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {


        if (sender instanceof Player player) {
            if (args.length > 5) {
                new BukkitRunnable() {
                    int t = 200;
                    @Override
                    public void run() {
                        if (t <= 0) cancel();
                        player.getWorld().spawnParticle(Particle.DUST, ArmorStandUtil.getRelativeLocation(player.getLocation(), 1, 0, 0, 0, 0), 1, 0, 0, 0, 0, new Particle.DustOptions(Color.RED, 0.5f));
                        t--;
                    }
                }.runTaskTimer(MangaX.getPlugin(MangaX.class), 1, 1);
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
