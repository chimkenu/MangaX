package me.chimkenu.mangax;

import com.destroystokyo.paper.profile.PlayerProfile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.URI;

public class SkullUtil {
    public static ItemStack getSkull(String skin) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer("__Star_Platinum_"));
        PlayerProfile playerProfile = meta.getPlayerProfile();
        if (playerProfile == null) {
            Bukkit.broadcastMessage("niga");
            return skull;
        }

        PlayerTextures textures = playerProfile.getTextures();
        try {
            textures.setSkin(new URI(skin).toURL());
        } catch (Exception ignored) {
            ignored.printStackTrace();
            return skull;
        }

        playerProfile.setTextures(textures);
        meta.setPlayerProfile(playerProfile);
        skull.setItemMeta(meta);
        return skull;
    }
}
