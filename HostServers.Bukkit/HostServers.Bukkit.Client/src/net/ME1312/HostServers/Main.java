package net.ME1312.HostServers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.*;
import java.util.*;

/**
 * The Main HostServers Class & Command Handler
 * This Requires Subservers v1.8.8f+ to work.
 *
 * @version 1.8.8e
 * @author ME1312
 *
 */
public class Main {

    public JavaPlugin Plugin;
    public int Servers = 0;
    public List<Integer> usedPorts = new ArrayList<Integer>();
    public HashMap<Player, Integer> playerLimit = new HashMap<Player, Integer>();

    public String lprefix;
    public HashMap<String, String> config = new HashMap<String, String>();

    protected Main(JavaPlugin plugin) throws IllegalArgumentException {
        if (plugin != null && plugin.getDescription().getName().equalsIgnoreCase("HostServerClient")) {
            Plugin = plugin;
        } else {
            throw new IllegalArgumentException("Main Should only be called by HostServers Plugin.");
        }
    }

    protected void EnablePlugin() {
        PluginManager pm = Bukkit.getServer().getPluginManager();
        lprefix = "[" + Plugin.getDescription().getName() + "] ";
        if (!(new File(Plugin.getDataFolder().toString())).exists()) {
            new File(Plugin.getDataFolder().toString()).mkdirs();
        }

        Plugin.getCommand("host").setExecutor(new HostCommand(this));
        Plugin.getCommand("hostserver").setExecutor(new HostCommand(this));
        Plugin.getCommand("hostconfig@client").setExecutor(new DebugCommand(this));

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Plugin, new Runnable() {
            @Override
            public void run() {
                if (config.get("owner") != null) {
                    if (Bukkit.getPlayer(config.get("owner")) == null || !Bukkit.getPlayer(config.get("owner")).isOnline()) {
                        Bukkit.getLogger().info(config.get("owner") + " is not Online, Shutting down");
                        Bukkit.getServer().shutdown();
                    }
                }
            }
        }, 0L, (long) ((2 * 20) * 60));
    }


    protected void DisablePlugin() {
        Bukkit.getLogger().info(lprefix + "Plugin Disabled.");
    }
}
