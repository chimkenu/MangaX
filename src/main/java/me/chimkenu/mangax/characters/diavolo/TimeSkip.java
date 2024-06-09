package me.chimkenu.mangax.characters.diavolo;

import me.chimkenu.mangax.utils.RayTrace;
import me.chimkenu.mangax.characters.Move;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class TimeSkip extends Move {
    public TimeSkip() {
        super((plugin, player) -> {
            RayTrace ray = new RayTrace(player.getEyeLocation().toVector(), player.getEyeLocation().getDirection());
            Location last = player.getLocation();
            for (Vector v : ray.traverse(20, 0.2)) {
                Block block = v.toLocation(player.getWorld()).getBlock();
                if (!block.isPassable()) {
                    player.teleport(last);
                    return;
                }
                last = block.getLocation();
                last.setDirection(player.getLocation().getDirection());
                last.add(new Vector(0.5, 0.5, 0.5));
            }
            player.teleport(last);
        }, null, 0, 25 * 20, Material.TIPPED_ARROW, Component.text("Time Skip").color(NamedTextColor.RED).decorate(TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
    }

    @Override
    public ItemStack getItem() {
        ItemStack item = new ItemStack(getMaterial());
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setBasePotionType(PotionType.HEALING);
        meta.displayName(getName());
        item.setItemMeta(meta);
        return item;
    }

    @Override
    public ArrayList<Component> getLore() {
        return new ArrayList<>();
    }
}
