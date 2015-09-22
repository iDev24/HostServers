package net.ME1312.HostServers;

import net.ME1312.SubServer.GUI.*;
import net.ME1312.SubServer.Libraries.Events.SubEvent;
import net.ME1312.SubServer.Libraries.Events.SubListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by ME1312 on 9/22/15.
 */
public class ServerListener extends SubListener {
    private Main Main;

    public ServerListener(Main Main) {
        this.Main = Main;
    }


    @Override
    public void onSubServerStart(SubEvent.SubPlayerEvent event) { return; }

    /**
     * Called when a Subserver is Stopped/Terminated
     *
     * @param event The event attached to this method
     */
    @Override
    public void onSubServerStop(SubEvent.SubPlayerEvent event) { return; }

    /**
     * Called when a Subserver's Shell Exits
     *
     * @param event The event attached to this method
     */
    @Override
    public void onSubServerShellExit(SubEvent event) { return; }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (event.getInventory().getName().contains(ChatColor.DARK_GREEN + Main.lang.getString("Lang.GUI.Title").replace("$Int$", ""))) {
            Player player = (Player) event.getWhoClicked();
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR && event.getCurrentItem().hasItemMeta()) {
                if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_RED.toString() + Main.lang.getString("Lang.GUI.Exit"))) {
                    player.closeInventory();
                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_GREEN.toString() + Main.lang.getString("Lang.GUI.Back"))) {

                    new GUI(player, (Integer.parseInt(event.getClickedInventory().getName().replace(ChatColor.DARK_GREEN.toString() + Main.lang.getString("Lang.GUI.Title").replace("$Int$", ""), "")) - 2), Main);

                } else if (event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.DARK_GREEN.toString() + Main.lang.getString("Lang.GUI.Next"))) {

                    new GUI(player, Integer.parseInt(event.getClickedInventory().getName().replace(ChatColor.DARK_GREEN.toString() + Main.lang.getString("Lang.GUI.Title").replace("$Int$", ""), "")), Main);

                } else if (!event.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.GRAY + Main.Plugin.getDescription().getName() + " v" + Main.Plugin.getDescription().getVersion())) {

                    Main.HostServer(player, event.getCurrentItem().getItemMeta().getDisplayName().replace(ChatColor.YELLOW.toString(), ""));
                    player.closeInventory();
                }
                event.setCancelled(true);
            }
        }
    }
}
