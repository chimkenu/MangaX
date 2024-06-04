package me.chimkenu.mangax;

import org.bukkit.Bukkit;
import org.bukkit.entity.ArmorStand;

public class ArmorStandUtil {
    public static void setUpArmorStand(ArmorStand armorStand) {
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setBasePlate(false);
    }

    public static void runCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
