package net.ME1312.HostServers;

import net.ME1312.SubServer.API;
import net.ME1312.SubServer.Executable.Executable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
/**
 * Created by ME1312 on 9/22/15.
 */
public class HostCommand implements CommandExecutor {
    private Main Main;

    public HostCommand(Main Main) {
        this.Main = Main;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String s, final String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("version") || args[0].equalsIgnoreCase("ver")) {
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + " " + Main.Plugin.getDescription().getName() + " v" + Main.Plugin.getDescription().getVersion() + ChatColor.GREEN + ChatColor.ITALIC + " © ME1312 EPIC 2015");
                    ((Player) sender).sendMessage(" ");
                    ((Player) sender).sendMessage(ChatColor.AQUA + " Project Page:" + ChatColor.ITALIC + " " + Main.Plugin.getDescription().getWebsite());
                } else {
                    Bukkit.getLogger().info(Main.Plugin.getDescription().getName() + " v" + Main.Plugin.getDescription().getVersion() + " © ME1312 EPIC 2015");
                    Bukkit.getLogger().info("Project Page: " + Main.Plugin.getDescription().getWebsite());
                }
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (!(sender instanceof Player) || ((Player) sender).hasPermission("hostserver.reload")) {
                    Main.config.reloadConfig();
                    Main.lang.reloadConfig();
                    if (sender instanceof Player) {
                        ((Player) sender).sendMessage(ChatColor.AQUA + Main.lprefix + Main.lang.getString("Lang.Commands.Config-Reload"));
                    }
                }
                Bukkit.getLogger().info(Main.lprefix + Main.lang.getString("Lang.Commands.Config-Reload"));
            } else if (Main.config.getConfigurationSection("Templates").getKeys(false).contains(args[0]) && (sender instanceof Player)) {
                if (sender instanceof Player) {
                    Main.HostServer((Player) sender, args[0]);
                } else {
                    Bukkit.getLogger().info(Main.lang.getString("Lang.Commands.HostServ-Console-Error"));
                }
            } else {
                if (sender instanceof Player) {
                    ((Player) sender).sendMessage(ChatColor.RED + Main.lprefix + Main.lang.getString("Lang.Commands.HostServ-Invalid"));
                } else {
                    Bukkit.getLogger().info(Main.lang.getString("Lang.Commands.HostServ-Invalid"));
                }
            }
        } else {
            if (sender instanceof Player) {
                if (Main.config.getBoolean("Settings.GUI") && ((Player) sender).hasPermission("hostserver.GUI")) {
                    new GUI((Player) sender, 0, Main);
                } else {
                    ((Player) sender).sendMessage("HostServers Command List:");
                    ((Player) sender).sendMessage("HostServer: /HostServer <Template>");
                    ((Player) sender).sendMessage("Reload: /HostServer Reload");
                    ((Player) sender).sendMessage("Version: /HostServer Version");
                }
            } else {
                Bukkit.getLogger().info("Main HostServers Command");
                Bukkit.getLogger().info("HostServer: /HostServer <Template>");
                Bukkit.getLogger().info("Reload: /HostServer Reload");
                Bukkit.getLogger().info("Version: /HostServer Version");
            }
        }
        return true;
    }
}