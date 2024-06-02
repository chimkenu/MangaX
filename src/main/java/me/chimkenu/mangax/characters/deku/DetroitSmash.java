package me.chimkenu.mangax.characters.deku;

import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;

public class DetroitSmash extends Move {
    public DetroitSmash() {
        super((plugin, player) -> {
            Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "wooper " + player.getName() + " " + player.getX() + " " + player.getEyeLocation().getY() + " " + player.getZ() + " " + player.getYaw() + " " + player.getPitch() + " spit 5 15 0 0");
            Location loc = player.getEyeLocation();
            loc.add(loc.getDirection().multiply(3));
            for (LivingEntity e : loc.getNearbyLivingEntities(3)) {
                if (e != player) {
                    e.damage(12, player);
                }
            }
        }, null, 15 * 20, Material.RAW_IRON, Component.text("Detroit Smash").color(TextColor.fromHexString("#106761")), new ArrayList<>());
    }
}
