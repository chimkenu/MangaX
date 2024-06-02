package me.chimkenu.mangax;

import me.chimkenu.mangax.commands.GetItem;
import me.chimkenu.mangax.listeners.Listeners;
import me.chimkenu.mangax.listeners.MoveListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class MangaX extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getCommand("getitems").setExecutor(new GetItem());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
