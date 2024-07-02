package me.chimkenu.mangax;

import me.chimkenu.mangax.commands.GetItem;
import me.chimkenu.mangax.commands.KitCommand;
import me.chimkenu.mangax.commands.Summon;
import me.chimkenu.mangax.commands.TruceCommand;
import me.chimkenu.mangax.listeners.*;
import me.chimkenu.mangax.utils.BlockEffects;
import org.bukkit.plugin.java.JavaPlugin;

public final class MangaX extends JavaPlugin {

    @Override
    public void onEnable() {
        reloadConfig();

        getServer().getPluginManager().registerEvents(new MoveListener(this), this);
        getServer().getPluginManager().registerEvents(new DashListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(), this);
        getServer().getPluginManager().registerEvents(new GUIListener(), this);
        getServer().getPluginManager().registerEvents(new DamageListener(this), this);
        TruceListener truceListener = new TruceListener();
        getServer().getPluginManager().registerEvents(truceListener, this);

        getCommand("sommun").setExecutor(new Summon());
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("truce").setExecutor(new TruceCommand(truceListener));
    }

    @Override
    public void onDisable() {
        saveConfig();
        BlockEffects.revertAllChanges();
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();
    }
}
