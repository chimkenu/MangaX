package me.chimkenu.mangax;

import me.chimkenu.mangax.commands.*;
import me.chimkenu.mangax.games.Lobby;
import me.chimkenu.mangax.listeners.*;
import me.chimkenu.mangax.utils.BlockEffects;
import org.bukkit.plugin.java.JavaPlugin;

public final class MangaX extends JavaPlugin {
    private static Lobby lobby = null;

    public static Lobby getLobby() {
        return lobby;
    }

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

        lobby = new Lobby(this);

        getCommand("sommun").setExecutor(new Summon());
        getCommand("kit").setExecutor(new KitCommand());
        getCommand("truce").setExecutor(new TruceCommand(truceListener));
        getCommand("worlddata").setExecutor(new WorldDataCommand(this));
        getCommand("duel").setExecutor(new DuelCommand(lobby));
        getCommand("spectate").setExecutor(new SpectateCommand(lobby));
        getCommand("addlore").setExecutor(new AddLoreCommand());
    }

    @Override
    public void onDisable() {
        lobby.stopAllGames();
        reloadConfig();
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
