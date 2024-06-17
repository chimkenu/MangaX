package me.chimkenu.mangax;

import me.chimkenu.mangax.commands.GetItem;
import me.chimkenu.mangax.commands.Summon;
import me.chimkenu.mangax.listeners.BlockListener;
import me.chimkenu.mangax.listeners.DashListener;
import me.chimkenu.mangax.listeners.MoveListener;
import me.chimkenu.mangax.utils.BlockEffects;
import org.bukkit.plugin.java.JavaPlugin;

public final class MangaX extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();
        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new DashListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getCommand("getitems").setExecutor(new GetItem());
        getCommand("sommun").setExecutor(new Summon());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BlockEffects.revertAllChanges();
    }
}
