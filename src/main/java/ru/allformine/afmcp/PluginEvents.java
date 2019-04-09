package ru.allformine.afmcp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.packet.Ambient;
import ru.allformine.afmcp.packet.TerritoryShow;

import java.util.HashMap;

public class PluginEvents {
    public static HashMap<Player, String> playerCurrentMusic = new HashMap<>();
    public static HashMap<Player, String> playerCurrentNamedRegion = new HashMap<>();

    private static String defaultTSText = ChatColor.GREEN + "www.AllForMine.ru";

    public static void quitOrJoin(Player player, boolean act) {
        boolean isStaff = player.hasPermission("afmcp.staff");
        String message;

        if (act) { //true - вошел в игру, false - вышел.
            TerritoryShow.sendTSPacketToPlayer(defaultTSText, player);

            message = " вошел в игру!";

            Discord.sendMessagePlayer(!isStaff ? Discord.MessageTypePlayer.TYPE_PLAYER_JOINED : Discord.MessageTypePlayer.TYPE_STAFF_JOINED, "", player);
        } else {
            message = " вышел из игры!";

            Discord.sendMessagePlayer(!isStaff ? Discord.MessageTypePlayer.TYPE_PLAYER_LEFT : Discord.MessageTypePlayer.TYPE_STAFF_LEFT, "", player);
        }

        if (isStaff) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission("afmcp.staff") && !p.equals(player)) {
                    p.sendMessage(ChatColor.DARK_AQUA + player.getName() + ChatColor.GREEN + message + ChatColor.DARK_AQUA + " (персонал)");
                }
            }
        }
    }

    public static void onPlayerRegionJoin(Player player, ProtectedRegion region) {
        System.out.println(player.getDisplayName() + " entered region " + region.getId());
        // ===============AmbientMusic
        String url = AFMCorePlugin.getPlugin().getConfig().getString("ambient_data." + region.getId() + ".url");

        if (url != null) {
            if(playerCurrentMusic.get(player) == null || !playerCurrentMusic.get(player).equals(url)) {
                Ambient.sendAmbientMusicPacket(false, player, url);
                playerCurrentMusic.put(player, url);
            }
        } else if (playerCurrentMusic.get(player) != null) {
            Ambient.sendAmbientMusicPacket(true, player, "");
            playerCurrentMusic.remove(player);
        }

        // ===============RGName
        String rgCustomName = AFMCorePlugin.getPlugin().getConfig().getString("rgname_data." + region.getId() + ".name");

        if (rgCustomName != null) {
            if(playerCurrentNamedRegion.get(player) == null || !playerCurrentNamedRegion.get(player).equals(rgCustomName)) {
                TerritoryShow.sendTSPacketToPlayer(rgCustomName, player);
                playerCurrentNamedRegion.put(player, rgCustomName);
            }
        } else if (playerCurrentNamedRegion.get(player) != null) {
            TerritoryShow.sendTSPacketToPlayer(defaultTSText, player);
            playerCurrentNamedRegion.remove(player);
        }
    }

    public static void onPlayerRegionLeft(Player player) {
        System.out.println(player.getDisplayName() + " left region");
        // ===============AmbientMusic
        if (playerCurrentMusic.get(player) != null) {
            Ambient.sendAmbientMusicPacket(true, player, "");
            playerCurrentMusic.remove(player);
        }

        // ===============RGName
        if (playerCurrentNamedRegion.get(player) != null) {
            TerritoryShow.sendTSPacketToPlayer(defaultTSText, player);
            playerCurrentNamedRegion.remove(player);
        }
    }
}
