package me.chimkenu.mangax.listeners;

import me.chimkenu.mangax.events.BlockBreakEvent;
import me.chimkenu.mangax.events.MoveTargetEvent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class BlockListener implements Listener {
    private static final HashMap<LivingEntity, Data> blocking = new HashMap<>();

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
        if (!blocking.containsKey(e.getTarget()) || e.isCancelled()) {
            return;
        }

        switch (block(e.getTarget(), e.getDamage())) {
            case BLOCK -> {
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 1.2f);
                e.setCancelled(true);
            }
            case BREAK -> {
                BlockBreakEvent event = new BlockBreakEvent(e.getTarget());
                Bukkit.getPluginManager().callEvent(event);

                toggleBlock(e.getTarget(), false);
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BREAK, SoundCategory.PLAYERS, 1, 1);
                e.setDamage(e.getDamage() * 0.5);
                e.getTarget().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 4, false, false, true));
            }
            case PERFECT_BLOCK -> {
                if (e.getTarget() instanceof Player target)
                    target.setFoodLevel(20);
                if (e.getSource() instanceof Player source) {
                    source.setFoodLevel(0);
                    Bukkit.getPluginManager().callEvent(new FoodLevelChangeEvent(source, 0));
                }
                e.getSource().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20, 100, false, false, true));
                e.getTarget().addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 20, 4, false, false, true));
                e.getTarget().getWorld().playSound(e.getTarget().getLocation(), Sound.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1, 0);
                e.setCancelled(true);
            }
        }
    }

    public static void toggleBlock(LivingEntity livingEntity, boolean putUpShield) {
        EntityEquipment equipment = livingEntity.getEquipment();

        if (blocking.containsKey(livingEntity) || !putUpShield) {
            Data data = blocking.remove(livingEntity);
            if (equipment != null && data != null)
                equipment.setItemInOffHand(data.previousItem);
        } else {
            ItemStack itemInOffHand = new ItemStack(Material.AIR);
            if (equipment != null) {
                itemInOffHand = equipment.getItemInOffHand();
                equipment.setItemInOffHand(new ItemStack(Material.SHIELD));
            }
            blocking.put(livingEntity, new Data(itemInOffHand, 3, 6));
        }
    }

    private BlockResult block(LivingEntity target, double damage) {
        blocking.putIfAbsent(target, new Data(target.getActiveItem(), 3, 6));
        Data data = blocking.get(target);

        data.hitsLeft--;
        data.damageLeft -= damage;

        long time = System.currentTimeMillis() - data.time;
        if (!data.hasBeenHit && time > 200 && time < 400) {
            return BlockResult.PERFECT_BLOCK;
        }
        data.hasBeenHit = true;

        if (data.hitsLeft < 0 || data.damageLeft <= 0) {
            return BlockResult.BREAK;
        }

        return BlockResult.BLOCK;
    }

    private static class Data {
        public final long time;
        public final ItemStack previousItem;
        public boolean hasBeenHit;
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
