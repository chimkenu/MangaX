package me.chimkenu.mangax.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class ArmorStandUtil {
    public static void setUpArmorStand(ArmorStand armorStand) {
        armorStand.setInvulnerable(true);
        armorStand.setInvisible(true);
        armorStand.setGravity(false);
        armorStand.setSilent(true);
        armorStand.setBasePlate(false);
        armorStand.addEquipmentLock(EquipmentSlot.HEAD, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.CHEST, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.LEGS, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.FEET, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
        armorStand.addEquipmentLock(EquipmentSlot.OFF_HAND, ArmorStand.LockType.REMOVING_OR_CHANGING);
    }

    public static void runCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }

    public static EulerAngle newEulerAngle(double x, double y, double z) {
        return new EulerAngle(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    public static Location getRelativeLocation(Location origin, double left, double up, double forward, float yaw, float pitch) {
        origin = origin.clone();
        Location result = origin.clone();

        Vector direction = origin.getDirection();

        float temp = origin.getPitch();
        origin.setPitch(0);
        origin.setYaw(origin.getYaw() - 90);
        Vector leftDirection = origin.getDirection();

        origin.setYaw(origin.getYaw() + 90);
        origin.setPitch(temp - 90);
        Vector upDirection = origin.getDirection();

        result = result.add(direction.multiply(forward)).add(leftDirection.multiply(left)).add(upDirection.multiply(up));
        result.setYaw(result.getYaw() + yaw);
        result.setPitch(result.getPitch() + pitch);

        return result.toLocation(origin.getWorld());
    }
}
