package me.chimkenu.mangax.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class BlockEffects {
    private static final HashMap<Location, DataTime> blockDataHashMap = new HashMap<>();

    public static void create(JavaPlugin plugin, Location location, BlockData newBlockData, int duration, Effect onRevert) {
        Location blockLocation = location.toBlockLocation();
        long now = System.currentTimeMillis();
        DataTime dataTime = new DataTime(blockLocation.getBlock().getState(), now);

        blockDataHashMap.putIfAbsent(blockLocation, dataTime);
        blockLocation.getBlock().setBlockData(newBlockData, false);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (blockDataHashMap.containsKey(location) && blockDataHashMap.get(location).timeEdited == now)
                    revert(location, onRevert);
            }
        }.runTaskLater(plugin, duration);
    }

    public static void revert(Location location, Effect onRevert) {
        Location blockLocation = location.toBlockLocation();
        DataTime data = blockDataHashMap.get(blockLocation);
        if (data != null) {
            onRevert.onRevert(location);
            data.blockState.update(true, false);
        }
        blockDataHashMap.remove(blockLocation);
    }

    public static void revertAllChanges() {
        for (DataTime dataTime : blockDataHashMap.values()) {
            dataTime.blockState.update(true, false);
        }
        blockDataHashMap.clear();
    }

    private static class DataTime {
        public final BlockState blockState;
        public long timeEdited;

        private DataTime(BlockState blockState, long timeEdited) {
            this.blockState = blockState;
            this.timeEdited = timeEdited;
        }
    }

    public interface Effect {
        void onRevert(Location location);
    }
}
