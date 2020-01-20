package ru.alfomine.afmcp.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.google.common.collect.Iterables;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

public class WrappedTabList {
    private List<WrappedTabListEntry> entries;

    public WrappedTabList() {
        this.entries = new ArrayList<>();
        Bukkit.getScheduler().runTaskTimer(AFMCorePlugin.getPlugin(), new TabListUpdateTask(this), 0, 100);
    }

    public void addEntry(Player player) {
        WrappedTabListEntry entry = new WrappedTabListEntry(player);
        if (this.entries.contains(entry)) return;
        this.entries.add(entry);
    }

    @SuppressWarnings("unused")
    public void removeEntry(Player player) {
        WrappedTabListEntry entryToRemove = null;

        for (WrappedTabListEntry entry : this.entries) {
            if (entry.uuid.equals(player.getUniqueId())) {
                entryToRemove = entry;
                break;
            }
        }

        if (entryToRemove != null) {
            this.entries.remove(entryToRemove);
        }
    }

    public List<WrappedTabListEntry> getEntries() { // Дает Immutable лист (неизменяемый)
        return this.entries;
    }

    public void clearClientside() {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for (Player player : onlinePlayers) {
            players.add(new PlayerInfoData(new WrappedGameProfile(player.getUniqueId(), player.getDisplayName()), -1,
                    EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText("")));
        }

        packet.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.setData(players);
        packet.broadcastPacket();
    }

    public void sendTabList() {
        WrapperPlayServerPlayerInfo packetServerInfo = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();

        for (WrappedTabListEntry entry : this.entries) {
            players.add(entry.getAsPlayerInfoData());

            entry.sendHeaderFooterPacket();
        }

        packetServerInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        packetServerInfo.setData(players);
        packetServerInfo.broadcastPacket();
    }

    // ================== Для таска ================= //

    void clearEntries() {
        this.entries.clear();
    }

    void sortEntries() {
        this.entries.sort((a, b) -> {
            String aName = Iterables.getLast(a.permissionUser.getParentIdentifiers(null), "player");
            String bName = Iterables.getLast(b.permissionUser.getParentIdentifiers(null), "player");
            AFMCorePlugin.log("a " + aName + "; b " + bName, Level.INFO);
            return Integer.compare(PluginConfig.tabSortGroups.indexOf(aName), PluginConfig.tabSortGroups.indexOf(bName));
        });
    }
}
