package ru.allformine.afmcp;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.packet.Ambient;
import ru.allformine.afmcp.packet.TerritoryShow;

class WGRegionEvent {
    static void OnPlayerEnterOrLeave(Player player) {
        RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());
        if (!regionManager.getApplicableRegions(player.getLocation()).getRegions().isEmpty()) {
            String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(player.getLocation()).getRegions()).getId();
            String url = AFMCorePlugin.getPlugin().getConfig().getString("ambient_data." + playerRegionName + ".url");

            if (url != null && (References.playerCurrentMusic.get(player) == null || !References.playerCurrentMusic.get(player).equals(playerRegionName))) {
                Ambient.sendAmbientMusicPacket(false, player, url);
                References.playerCurrentMusic.put(player, playerRegionName);
            } else if (url == null && References.playerCurrentMusic.get(player) != null) {
                Ambient.sendAmbientMusicPacket(true, player, "");
                References.playerCurrentMusic.remove(player);
            }

            String rgCustomName = AFMCorePlugin.getPlugin().getConfig().getString("rgname_data." + playerRegionName + ".name");

            if (rgCustomName != null && (References.playerCurrentNamedRegion.get(player) == null || !References.playerCurrentNamedRegion.get(player).equals(playerRegionName))) {
                TerritoryShow.sendTSPacketToPlayer(rgCustomName, player);
                References.playerCurrentNamedRegion.put(player, playerRegionName);
            } else if (rgCustomName == null && References.playerCurrentNamedRegion.get(player) != null) {
                TerritoryShow.sendTSPacketToPlayer(ChatColor.BLUE + "SpaceUnion ОБТ", player);
                References.playerCurrentNamedRegion.remove(player);
            }
        } else if (References.playerCurrentMusic.get(player) != null) {
            Ambient.sendAmbientMusicPacket(true, player, "");
            References.playerCurrentMusic.remove(player);
        } else if (References.playerCurrentNamedRegion.get(player) != null) {
            TerritoryShow.sendTSPacketToPlayer(ChatColor.BLUE + "SpaceUnion ОБТ", player);
            References.playerCurrentNamedRegion.remove(player);
        }
    }
}
