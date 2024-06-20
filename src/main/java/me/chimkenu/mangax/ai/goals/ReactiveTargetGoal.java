package me.chimkenu.mangax.ai.goals;

import me.chimkenu.mangax.ai.action.Action;
import me.chimkenu.mangax.ai.action.BlockAction;
import me.chimkenu.mangax.ai.action.DashAction;
import me.chimkenu.mangax.ai.action.WaitAction;
import me.chimkenu.mangax.ai.movement.RushStrafe;
import me.chimkenu.mangax.enums.MoveInfo;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTriggerEvent;
import me.chimkenu.mangax.utils.RandomUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Set;

import static me.chimkenu.mangax.utils.RandomUtil.isAround;
import static me.chimkenu.mangax.utils.RandomUtil.randomFrom;

public class ReactiveTargetGoal extends TargetGoal {
    public ReactiveTargetGoal(JavaPlugin plugin, Mob mob, Set<Moves> moveSet) {
        super(plugin, mob, moveSet);
    }

    @Override
    public Action generateNextAction() {
        Bukkit.broadcastMessage("generating next action");

        // get list of available moves
        ArrayList<Action> actions = new ArrayList<>();
        moveMap.forEach((moves, cooldown) -> {
            MoveInfo info = moves.move.getMoveInfo();
            if (cooldown <= 0) actions.add(new Action(this, info.chargeTime() + info.duration(), new RushStrafe(20, 1), null) {
                @Override
                public void start() {
                    super.start();
                    mob.getEquipment().setItemInMainHand(moves.move.getItem());
                    moves.move.getActivate().activate(plugin, mob);
                    goal.moveMap.put(moves, moves.move.getCooldown());
                }

                @Override
                public boolean preconditions() {
                    LivingEntity target = mob.getTarget();
                    return target != null && isAround(mob.getLocation().distanceSquared(target.getLocation()), info.recommendedRange(), 16);
                }
            });
        });

        // add general actions (wait, dash, block)
        actions.add(new WaitAction(this, 5, new RushStrafe(20, 1), null));
        actions.add(new DashAction(this, mob.getLocation().getDirection(), null));
        actions.add(new BlockAction(this, 10, null));

        // pick one at random?
        return actions.get(randomFrom(0, actions.size()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMoveTrigger(MoveTriggerEvent e) {
        if (e.isCancelled() || e.getEntity() != mob.getTarget()) {
            return;
        }
        Location loc = mob.getLocation();
        loc.setYaw(loc.getYaw() + 90);
        loc.setPitch(-10);

        if (RandomUtil.probability(0.4)) {
            interrupt(new DashAction(this, loc.getDirection(), null));
        } else {
            interrupt(new BlockAction(this, e.getMove().move.getMoveInfo().duration() + 10, null));
        }
    }
}
