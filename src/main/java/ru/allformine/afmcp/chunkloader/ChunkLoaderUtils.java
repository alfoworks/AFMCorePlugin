//АХТУНГ - недописано!

package ru.allformine.afmcp.chunkloader;

import org.bukkit.entity.Player;
import ru.allformine.afmcp.AFMCorePlugin;

public class ChunkLoaderUtils {
    public static boolean CheckLoadPerm(Player p, String chunk_id) {
        int permcount = 0;
        int playercount = 0;

        boolean ret = false;

        for (String group : AFMCorePlugin.getPlugin().getConfig().getConfigurationSection("loader_groups").getKeys(false)) {
            if (p.hasPermission("afmcp.loader." + group)) {
                permcount = AFMCorePlugin.getPlugin().getConfig().getInt("loader_groups." + group + ".count");
            }
        }

        for (String loader : AFMCorePlugin.getPlugin().getConfig().getConfigurationSection("loaders").getKeys(false)) {
            if (AFMCorePlugin.getPlugin().getConfig().getString("loaders." + loader + ".owner").equals(p.getName())) {
                playercount++;

                if (playercount > permcount) {
                    AFMCorePlugin.getPlugin().getConfig().set("loaders." + loader, null);

                    ret = !loader.equals(chunk_id) || ret;
                }
            }
        }
        AFMCorePlugin.getPlugin().saveConfig();
        return ret;
    }
}
