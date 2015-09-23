package net.ME1312.HostServers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

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
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (sender instanceof Player) {
                    if (((Player) sender).getName().equalsIgnoreCase(Main.config.get("owner"))) {
                        Bukkit.getServer().shutdown();
                    }
                } else {
                    Bukkit.getServer().shutdown();
                }
            }
        } else {
            if (sender instanceof Player) {
                ((Player) sender).sendMessage("HostServers Command List:");
                ((Player) sender).sendMessage("Stop Server: /HostServer Stop");
                ((Player) sender).sendMessage("Version: /HostServer Version");
            } else {
                Bukkit.getLogger().info("Main HostServers Command");
                Bukkit.getLogger().info("Stop Server: /HostServer Stop");
                Bukkit.getLogger().info("Version: /HostServer Version");
            }
        }
        return true;
    }
}