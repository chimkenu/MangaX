package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.events.MoveTargetEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public class BlockListener implements Listener {
    private final HashMap<LivingEntity, Data> blocking;

    public BlockListener() {
        blocking = new HashMap<>();
    }

    @EventHandler
    public void onSneakToggle(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if (!e.isSneaking() || !player.getGameMode().equals(GameMode.ADVENTURE)) {
            Data data = blocking.remove(player);
            if (data != null)
                player.getInventory().setItemInOffHand(data.previousItem);
            return;
        }

        blocking.put(player, new Data(player.getInventory().getItemInOffHand(), 3, 6));
        player.getInventory().setItemInOffHand(new ItemStack(Material.SHIELD));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onMoveTarget(MoveTargetEvent e) {
        if (!blocking.containsKey(e.getTarget())) {
            return;
        }

        switch (block(e.getTarget(), e.getDamage())) {
            case BLOCK -> {
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1.2f);
                e.setCancelled(true);
            }
            case BREAK -> {
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1, 1);
                e.setDamage(e.getDamage() * 1.5);
            }
            case PERFECT_BLOCK -> {
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 0);
                e.setCancelled(true);
            }
        }
    }

    private BlockResult block(LivingEntity target, double damage) {
        blocking.putIfAbsent(target, new Data(target.getActiveItem(), 3, 6));
        Data data = blocking.get(target);

        data.hitsLeft--;
        data.damageLeft -= damage;
        if (data.hitsLeft < 0 || data.damageLeft <= 0) {
            return BlockResult.BREAK;
        }

        long time = System.currentTimeMillis() - data.time;
        if (time > 200 && time < 400) {
            return BlockResult.PERFECT_BLOCK;
        }

        return BlockResult.BLOCK;
    }

    private static class Data {
        public final long time;
        public final ItemStack previousItem;
        public int hitsLeft;
        public double damageLeft;

        public Data(ItemStack previousItem, int hitsLeft, double damageLeft) {
            this.previousItem = previousItem;
            time = System.currentTimeMillis();
            this.hitsLeft = hitsLeft;
            this.damageLeft = damageLeft;
        }
    }

    private enum BlockResult {
        BREAK,
        BLOCK,
        PERFECT_BLOCK
    }
}
