package net.ME1312.HostServers;

import net.ME1312.SubServer.API;
import net.ME1312.SubServer.Libraries.Version.Version;
import org.bukkit.plugin.java.JavaPlugin;

public class Load extends JavaPlugin {
    private Main Main;
    private Version SubVersion = new Version("1.8.8g");

    @Override
    public void onEnable() {
        if (API.getPluginVersion().compareTo(SubVersion) >= 0) {
            try {
                Main = new Main(this);
                Main.EnablePlugin();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                setEnabled(false);
            }
        } else {
            new IllegalStateException("Required Version of SubServers not Found: " + SubVersion.toString()).printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        if (Main != null) {
            Main.DisablePlugin();
        }
    }

}
