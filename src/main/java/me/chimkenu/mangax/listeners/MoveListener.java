package me.chimkenu.mangax.listeners;

import io.papermc.paper.event.player.PlayerArmSwingEvent;
import me.chimkenu.mangax.characters.Move;
import me.chimkenu.mangax.characters.Punch;
import me.chimkenu.mangax.enums.Moves;
import me.chimkenu.mangax.events.MoveTargetEvent;
import me.chimkenu.mangax.events.MoveTriggerEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MoveListener implements Listener {
    private final JavaPlugin plugin;
    private final HashMap<UUID, Long> players;

    public MoveListener(JavaPlugin plugin) {
        this.plugin = plugin;
        for (Moves move : Moves.values()) {
            if (move.move instanceof Listener listener)
                plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        }
        this.players = new HashMap<>();
    }

    private void activateMove(Player player) {
        Moves move = Moves.getMoveFromItem(player.getInventory().getItemInMainHand());
        if (move == null) {
            return;
        }

        Move m = move.move;
        if (player.getCooldown(m.getMaterial()) >= m.getCooldown()) {
            if (m.getFollowUp() == null)
                return;

            MoveTriggerEvent event = new MoveTriggerEvent(player, move, true);
            Bukkit.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return;
            }

            m.getFollowUp().activate(plugin, player);
            player.setCooldown(m.getMaterial(), m.getCooldown());
            return;
        }

        MoveTriggerEvent event = new MoveTriggerEvent(player, move, false);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        m.getActivate().activate(plugin, player);
        if (player.getCooldown(m.getMaterial()) == 0)
            player.setCooldown(m.getMaterial(), m.getCooldown() + m.getFollowUpTime());

        if (m.getFollowUp() != null) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isDead() || !player.isOnline()) {
                        return;
                    }

                    if (player.getCooldown(m.getMaterial()) == m.getCooldown()) {
                        MoveTriggerEvent followUp = new MoveTriggerEvent(player, move, true);
                        Bukkit.getPluginManager().callEvent(followUp);
                        if (followUp.isCancelled()) {
                            return;
                        }

                        m.getFollowUp().activate(plugin, player);
                    }
                }
            }.runTaskLater(plugin, m.getFollowUpTime() + 1);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onMoveTrigger(MoveTriggerEvent e) {
        if (e.getEntity() instanceof Player player) {
            int cooldown = player.getCooldown(e.getMove().move.getMaterial());
            if (cooldown > 0 && cooldown < e.getMove().move.getCooldown()) {
                e.cancel(MoveTriggerEvent.CancelReason.IN_COOLDOWN);
            } else if (player.hasPotionEffect(PotionEffectType.HUNGER)) {
                e.cancel(MoveTriggerEvent.CancelReason.STUNNED);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLeftClick(PlayerArmSwingEvent e) {
        if (isValidTime(e.getPlayer()))
            activateMove(e.getPlayer());
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player player) || !(e.getEntity() instanceof LivingEntity target)) {
            return;
        }

        if (target.getType().equals(EntityType.ARMOR_STAND)) {
            return;
        }

        if (!isValidTime(player)) {
            return;
        }
        players.put(player.getUniqueId(), System.currentTimeMillis());

        if (player == target) {
            return;
        }

        e.setCancelled(true);
        ItemStack item = player.getInventory().getItemInMainHand();

        // check if player is using a move and if it implements the Punch interface
        Moves move = Moves.getMoveFromItem(item);
        if (move == null) {
            player.sendActionBar(Component.text("You can only damage opponents using moves!", NamedTextColor.RED));
            return;
        }
        Move m = move.move;
        if (!(m instanceof Punch punch))
            return;

        boolean isFollowUp = player.getCooldown(m.getMaterial()) >= m.getCooldown();
        MoveTriggerEvent event = new MoveTriggerEvent(player, move, isFollowUp);
        Bukkit.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        punch.punch(plugin, player, target, player.getCooldown(m.getMaterial()) >= m.getCooldown());
        if (isFollowUp) {
            if (m.getFollowUp() != null) {
                m.getFollowUp().activate(plugin, player);
                player.setCooldown(m.getMaterial(), m.getCooldown());
            }
        } else {
            m.getActivate().activate(plugin, player);
            if (player.getCooldown(m.getMaterial()) == 0)
                player.setCooldown(m.getMaterial(), m.getCooldown() + m.getFollowUpTime());
        }

    }

    private boolean isValidTime(Player player) {
        Long data = players.get(player.getUniqueId());
        if (data != null) {
            long diff = System.currentTimeMillis() - data;
            return diff > 10;
        }
        return true;
    }

    @EventHandler
    public void onDash(PlayerDropItemEvent e) {
        players.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onMoveTarget(MoveTargetEvent e) {
        if (e.getSource() instanceof Player source) {
            players.put(source.getUniqueId(), System.currentTimeMillis());
        }

        if (e.getTarget() instanceof Player target && target.getGameMode() != GameMode.ADVENTURE) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = e.getPlayer().getInventory().getItem(i);
            if (item != null) {
                e.getPlayer().setCooldown(item.getType(), 0);
            }
        }
    }
}
