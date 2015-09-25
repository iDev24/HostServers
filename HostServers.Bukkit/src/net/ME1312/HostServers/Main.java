package net.ME1312.HostServers;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.google.common.io.Files;
import net.ME1312.SubServer.Executable.Executable;
import net.ME1312.SubServer.Libraries.Config.ConfigFile;
import net.ME1312.SubServer.Libraries.Config.ConfigManager;
import net.ME1312.SubServer.Libraries.Metrics;
import net.ME1312.SubServer.API;

import net.ME1312.SubServer.Libraries.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
    public ConfigFile config;
    public ConfigFile lang;

    public Version PluginVersion;
    public Version MCVersion;

    private ConfigManager confmanager;

    protected Main(JavaPlugin plugin) throws IllegalArgumentException {
        if (plugin != null && plugin.getDescription().getName().equalsIgnoreCase("HostServer")) {
            Plugin = plugin;
        } else {
            throw new IllegalArgumentException("Main Should only be called by HostServers Plugin.");
        }
    }

    protected void EnablePlugin() {
        confmanager = new ConfigManager(Plugin);
        PluginManager pm = Bukkit.getServer().getPluginManager();
        lprefix = Plugin.getDescription().getName() + " \u00BB ";
        if (!(new File(Plugin.getDataFolder().toString())).exists()) {
            new File(Plugin.getDataFolder().toString()).mkdirs();
        }

        PluginVersion = new Version(Plugin.getDescription().getVersion());
        MCVersion = new Version(Bukkit.getServer().getVersion().split("\\(MC\\: ")[1].split("\\)")[0]);
        Bukkit.getLogger().info(lprefix + "Loading Libraries for " + MCVersion);

        /**
         * Updates Configs if needed
         */
        if (!(new File(Plugin.getDataFolder() + File.separator + "config.yml").exists())) {
            copyFromJar("config.yml", Plugin.getDataFolder() + File.separator + "config.yml");
            Bukkit.getLogger().info(lprefix + "Created Config.yml!");
        } else if (!confmanager.getNewConfig("config.yml").getString("Settings.config-version").equalsIgnoreCase("1.8.7a+")) {
            try {
                Files.move(new File(Plugin.getDataFolder() + File.separator + "config.yml"), new File(Plugin.getDataFolder() + File.separator + "old-config." + Math.round(Math.random() * 100000) + ".yml"));
                copyFromJar("config.yml", Plugin.getDataFolder() + File.separator + "config.yml");
                Bukkit.getLogger().info(lprefix + "Updated Config.yml!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!(new File(Plugin.getDataFolder() + File.separator + "lang.yml").exists())) {
            copyFromJar("lang.yml", Plugin.getDataFolder() + File.separator + "lang.yml");
            Bukkit.getLogger().info(lprefix + "Created Lang.yml!");
        } else if (!confmanager.getNewConfig("lang.yml").getString("config-version").equalsIgnoreCase("1.8.8b+")) {
            try {
                Files.move(new File(Plugin.getDataFolder() + File.separator + "lang.yml"), new File(Plugin.getDataFolder() + File.separator + "old-lang." + Math.round(Math.random() * 100000) + ".yml"));
                copyFromJar("lang.yml", Plugin.getDataFolder() + File.separator + "lang.yml");
                Bukkit.getLogger().info(lprefix + "Updated Lang.yml!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = confmanager.getNewConfig("config.yml");
        lang = confmanager.getNewConfig("lang.yml");

        Plugin.getCommand("host").setExecutor(new HostCommand(this));
        Plugin.getCommand("hostserver").setExecutor(new HostCommand(this));

        API.registerListener(new ServerListener(this), Plugin, true);

        try {
            new Metrics(Plugin).start();
        } catch (IOException e) {
            Bukkit.getLogger().warning(lprefix + "Couldn't Send info to MCStats");
            e.getStackTrace();
        }
    }


    protected void DisablePlugin() {
        Bukkit.getLogger().info(lprefix + "Plugin Disabled.");
    }

    public void HostServer(final Player Player, final String Template) {
        // Checks
        if (Player.hasPermission("hostserver.create.*") || Player.hasPermission("hostserver.create." + Template)) {
            if ((config.getInt("Settings.Server-Config.Max-Servers") - Servers) > 0) {
                if (playerLimit.get(Player) == null || playerLimit.get(Player) < 1) {

                    final int limit;
                    // Increase Player's Server count
                    if (playerLimit.get(Player) == null) {
                        playerLimit.put(Player, 1);
                        limit = 1;
                    } else {
                        limit = playerLimit.get(Player) + 1;
                        playerLimit.put(Player, limit);
                    }
                    Servers++;

                    new BukkitRunnable() {
                        public void run() {
                            Player.sendMessage(ChatColor.GOLD + lprefix + lang.getString("Lang.Commands.HostServ-Working"));

                            // Make Directory and Copy Template
                            if (!(new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit).exists())) {

                                if (!(new File(config.getString("Settings.Server-Config.Server-dir")).exists())) new File(config.getString("Settings.Server-Config.Server-dir")).mkdirs();
                                copyFolder(new File(config.getString("Templates." + Template + ".path")), new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit));
                            } else {

                                if (config.getBoolean("Settings.Server-Config.Remove-on-close")) {
                                    deleteDir(new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit));
                                    copyFolder(new File(config.getString("Templates." + Template + ".path")), new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit));
                                }
                            }

                            // Generate Port
                            int port = 0;
                            int i = 0;
                            do {
                                i = randInt(config.getInt("Settings.Port-Config.Min"), config.getInt("Settings.Port-Config.Max"));
                                if (!usedPorts.contains(i)) {
                                    port = i;
                                }
                            } while (port == 0);

                            //Adds Server to SubServers API
                            API.addServer(Player, "!" + Player.getName(), port, config.getBoolean("Templates." + Template + ".log"), new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit),
                                    new Executable(config.getString("Templates." + Template + ".shell") + " " + port), config.getDouble("Templates." + Template + ".stop-after"), true);
                            usedPorts.add(port);

                            // Checks Server Status
                            try {
                                Thread.sleep(10500); //TODO
                                if (API.getSubServer("!" + Player.getName()).isRunning()) {
                                    Player.sendMessage(ChatColor.AQUA + lprefix + lang.getString("Lang.Commands.HostServ").replace("$Player$", Player.getName()));
                                    API.getSubServer("!" + Player.getName()).sendCommandSilently("hostconfig@client setowner " + Player.getName());
                                    API.getSubServer("!" + Player.getName()).waitFor();
                                    Thread.sleep(1500);
                                } else {
                                    Player.sendMessage(ChatColor.RED + lprefix + lang.getString("Lang.Commands.HostServ-Start-Error"));
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Cleans up
                            Servers--;
                            usedPorts.remove((Object) port);
                            playerLimit.put(Player, limit - 1);
                            if (config.getBoolean("Settings.Server-Config.Remove-on-close")) {
                                deleteDir(new File(config.getString("Settings.Server-Config.Server-dir") + File.separatorChar + Player.getUniqueId().toString() + "-" + limit));
                            }
                        }
                    }.runTaskAsynchronously(Plugin);
                } else {
                    Player.sendMessage(ChatColor.RED + lprefix + lang.getString("Lang.Commands.HostServ-Player-Limit-Error"));
                }
            } else {
                Player.sendMessage(ChatColor.RED + lprefix + lang.getString("Lang.Commands.HostServ-Max-Servers-Error"));
            }
        } else {
            Player.sendMessage(ChatColor.RED + lprefix + lang.getString("Lang.Commands.HostServ-Permission-Error"));
        }
    }

    public int randInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    public void copyFolder(File source, File destination) {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String files[] = source.list();

            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);

                copyFolder(srcFile, destFile);
            }
        } else {
            InputStream in = null;
            OutputStream out = null;

            try {
                in = new FileInputStream(source);
                out = new FileOutputStream(destination);

                byte[] buffer = new byte[1024];

                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            } catch (Exception e) {
                try {
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                try {
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private void copyFromJar(String resource, String destination) {
        InputStream resStreamIn = Main.class.getClassLoader().getResourceAsStream(resource);
        File resDestFile = new File(destination);
        try {
            OutputStream resStreamOut = new FileOutputStream(resDestFile);
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = resStreamIn.read(buffer)) > 0) {
                resStreamOut.write(buffer, 0, readBytes);
            }
            resStreamOut.close();
            resStreamIn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
