package ru.allformine.afmcp;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerEvent;
import ru.allformine.afmcp.packet.Ambient;
import ru.allformine.afmcp.packet.TerritoryShow;

class WGRegionEvent {
    static void OnPlayerEnterOrLeave(PlayerEvent event) {
        RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
        if (!regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions().isEmpty()) {
            String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions()).getId();
            String url = AFMCorePlugin.getPlugin().getConfig().getString("ambient_data." + playerRegionName + ".url");

            if (url != null && (References.playerCurrentMusic.get(event.getPlayer()) == null || !References.playerCurrentMusic.get(event.getPlayer()).equals(playerRegionName))) {
                Ambient.sendAmbientMusicPacket(false, event.getPlayer(), url);
                References.playerCurrentMusic.put(event.getPlayer(), playerRegionName);
            } else if (url == null && References.playerCurrentMusic.get(event.getPlayer()) != null) {
                Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
                References.playerCurrentMusic.remove(event.getPlayer());
            }

            String rgCustomName = AFMCorePlugin.getPlugin().getConfig().getString("rgname_data." + playerRegionName + ".name");

            if (rgCustomName != null && (References.playerCurrentNamedRegion.get(event.getPlayer()) == null || !References.playerCurrentNamedRegion.get(event.getPlayer()).equals(playerRegionName))) {
                TerritoryShow.sendTSPacketToPlayer(rgCustomName, event.getPlayer());
                References.playerCurrentNamedRegion.put(event.getPlayer(), playerRegionName);
            } else if (rgCustomName == null && References.playerCurrentNamedRegion.get(event.getPlayer()) != null) {
                TerritoryShow.sendTSPacketToPlayer(ChatColor.BLUE + "SpaceUnion ОБТ", event.getPlayer());
                References.playerCurrentNamedRegion.remove(event.getPlayer());
            }
        } else if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
            Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
            References.playerCurrentMusic.remove(event.getPlayer());
        } else if (References.playerCurrentNamedRegion.get(event.getPlayer()) != null) {
            TerritoryShow.sendTSPacketToPlayer(ChatColor.BLUE + "SpaceUnion ОБТ", event.getPlayer());
            References.playerCurrentNamedRegion.remove(event.getPlayer());
        }
    }
}
