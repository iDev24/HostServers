package net.ME1312.HostServers;

import net.ME1312.SubServer.Libraries.Version.Version;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

/**
 * Created by ME1312 on 9/22/15.
 */
@SuppressWarnings("deprecation")
public class GUI {

    public GUI(Player player, int page, Main Main) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GREEN + Main.lang.getString("Lang.GUI.Title").replace("$Int$", Integer.toString(page + 1)));

        int i = 0;
        int min = (page * 18);
        int max = (min + 17);

        ItemStack block = null;
        ItemMeta blockMeta = null;
        List<String> Templates = new ArrayList<String>();
        Templates.addAll(Main.config.getConfigurationSection("Templates").getKeys(false));

        for(Iterator<String> str = Templates.iterator(); str.hasNext(); ) {
            String item = str.next();
            if (Main.config.getBoolean("Templates." + item + ".enabled")) {
                if (Templates.indexOf(item) >= min && Templates.indexOf(item) <= max) {
                    block = new ItemStack(289);
                    blockMeta = block.getItemMeta();
                    blockMeta.setDisplayName(ChatColor.YELLOW + item);
                    block.setItemMeta(blockMeta);
                    inv.setItem(i, block);
                    block = null;
                    blockMeta = null;
                }
            }
        }

        block = new ItemStack(Material.ENCHANTED_BOOK);
        blockMeta = block.getItemMeta();
        blockMeta.setDisplayName(ChatColor.GRAY + Main.Plugin.getName() + " v" + Main.Plugin.getDescription().getVersion());
        blockMeta.setLore(Arrays.asList("\u00A9 ME1312 EPIC 2015", "", ChatColor.DARK_AQUA + Main.lang.getString("Lang.GUI.Host-Help-Book").split("\\|\\|\\|")[0], ChatColor.DARK_AQUA + Main.lang.getString("Lang.GUI.Sub-Help-Book").split("\\|\\|\\|")[1]));
        block.setItemMeta(blockMeta);
        inv.setItem(18, block);
        block = null;
        blockMeta = null;

        if (Templates.size() > max) {
            block = new ItemStack(Material.IRON_INGOT);
            blockMeta = block.getItemMeta();
            blockMeta.setDisplayName(ChatColor.DARK_GREEN + Main.lang.getString("Lang.GUI.Next"));
            block.setItemMeta(blockMeta);
            inv.setItem(23, block);
            block = null;
            blockMeta = null;
        }

        if (min != 0) {
            block = new ItemStack(Material.IRON_INGOT);
            blockMeta = block.getItemMeta();
            blockMeta.setDisplayName(ChatColor.DARK_GREEN + Main.lang.getString("Lang.GUI.Back"));
            block.setItemMeta(blockMeta);
            inv.setItem(21, block);
            block = null;
            blockMeta = null;
        }

        if (Main.MCVersion.compareTo(new Version("1.8")) >= 0) {
            block = new ItemStack(166);
        } else {
            block = new ItemStack(Material.REDSTONE_BLOCK);
        }
        blockMeta = block.getItemMeta();
        blockMeta.setDisplayName(ChatColor.DARK_RED + Main.lang.getString("Lang.GUI.Exit"));
        block.setItemMeta(blockMeta);
        inv.setItem(26, block);
        block = null;
        blockMeta = null;

        player.openInventory(inv);
        inv = null;
    }
}
