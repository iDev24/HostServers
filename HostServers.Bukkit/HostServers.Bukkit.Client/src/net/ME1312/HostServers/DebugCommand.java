package net.ME1312.HostServers;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Created by ME1312 on 9/23/15.
 */
public class DebugCommand implements CommandExecutor {
    Main Main;

    public DebugCommand(Main Main) {
        this.Main = Main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            if (args[0].equalsIgnoreCase("setowner")) {
                Main.config.put("owner", args[1]);
                Bukkit.getLogger().info("Owner is " + Main.config.get("owner"));
            }
        } else {
            return false;
        }
        return true;
    }
}
