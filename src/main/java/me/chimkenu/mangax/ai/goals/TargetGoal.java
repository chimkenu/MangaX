package me.chimkenu.mangax.ai.goals;

import com.destroystokyo.paper.entity.ai.Goal;
import com.destroystokyo.paper.entity.ai.GoalKey;
import com.destroystokyo.paper.entity.ai.GoalType;
import me.chimkenu.mangax.MangaX;
import me.chimkenu.mangax.ai.action.Action;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.listeners.DashListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

public abstract class TargetGoal implements Goal<Mob>, Listener {
    private final GoalKey<Mob> goalKey = GoalKey.of(Mob.class, new NamespacedKey(MangaX.getPlugin(MangaX.class), "goal.target"));
    private final EnumSet<GoalType> types = EnumSet.of(GoalType.TARGET);

    protected final JavaPlugin plugin;
    public final Mob mob;
    protected final HashMap<Moves, Integer> moveMap;
    protected Action action;
    protected double foodLevel;

    public TargetGoal(JavaPlugin plugin, Mob mob, Set<Moves> moveSet) {
        this.plugin = plugin;
        this.mob = mob;
        moveMap = new HashMap<>();
        moveSet.forEach(move -> moveMap.put(move, 0));
        foodLevel = 20;
    }

    @Override
    public boolean shouldActivate() {
        return !mob.isDead() && mob.getTarget() != null;
    }

    @Override
    public boolean shouldStayActive() {
        return shouldActivate();
    }

    @Override
    public void start() {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        foodLevel = 20;
    }

    @Override
    public void stop() {
        HandlerList.unregisterAll(this);
        action = null;
        foodLevel = 0;
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null)
            return;

        // Update (always look at target)
        mob.lookAt(target, 30, 30);
        moveMap.replaceAll((k, v) -> Math.max(0, moveMap.get(k) - 1));
        foodLevel = Math.min(20, foodLevel + (2f / DashListener.REGEN_RATE));

        // Move queue (attacks and stuff)
        if (action == null)
            action = generateNextAction();

        action.movement.run(mob, mob.getPathfinder(), target);
        if (action.hasStarted() || action.preconditions())
            action.tick();
        if (action.hasStopped())
            action = action.next;
    }

    public void interrupt(Action action) {
        if (this.action == null) {
            this.action = action;
            return;
        }
        action.next = this.action;
        this.action = action;
    }

    public boolean attemptDash(Vector direction, boolean checkFoodLevel) {
        if (!checkFoodLevel || foodLevel >= DashListener.DASH_COST && DashListener.dash(direction, mob)) {
            foodLevel -= DashListener.DASH_COST;
            return true;
        }
        return false;
    }

    public abstract Action generateNextAction();

    @Override
    public @NotNull GoalKey<Mob> getKey() {
        return goalKey;
    }

    @Override
    public @NotNull EnumSet<GoalType> getTypes() {
        return types;
    }

    @EventHandler
    public void onMobDeath(EntityDeathEvent e) {
        if (e.getEntity() == mob)
            stop();
    }
}
