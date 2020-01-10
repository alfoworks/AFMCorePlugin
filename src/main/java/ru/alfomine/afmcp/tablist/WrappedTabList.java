package ru.alfomine.afmcp.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.*;

public class WrappedTabList {
    private LinkedHashSet<WrappedTabListEntry> entries; //Linked потому, что требуется сортировка.

    public WrappedTabList() {
        this.entries = new LinkedHashSet<>();
        Bukkit.getScheduler().runTaskTimer(AFMCorePlugin.getPlugin(), new TabListUpdateTask(this), 0, 100);
    }

    public void addEntry(Player player) {
        this.entries.add(new WrappedTabListEntry(player));
    }

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

    public LinkedHashSet<WrappedTabListEntry> getEntries() { // Дает Immutable лист (неизменяемый)
        return this.entries;
    }

    public void sendPlayerInfo(){
        for(WrappedTabListEntry entry: this.entries){
            WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
            packet.setFooter(getStringAsWrappedChatComponent(entry.footer));
            packet.setHeader(getStringAsWrappedChatComponent(entry.header));
            packet.sendPacket(Bukkit.getPlayer(entry.uuid));
        }
    }

    public void clearClientside(){
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        for(Player player: onlinePlayers){
            players.add(new PlayerInfoData(new WrappedGameProfile(player.getUniqueId(), player.getDisplayName()), -1,
                    EnumWrappers.NativeGameMode.NOT_SET, WrappedChatComponent.fromText("")));
        }

        packet.setAction(EnumWrappers.PlayerInfoAction.REMOVE_PLAYER);
        packet.setData(players);
        packet.broadcastPacket();
    }

    public void flush() {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();
        for (WrappedTabListEntry entry : this.entries) {
            players.add(new PlayerInfoData(new WrappedGameProfile(entry.uuid, "user"), entry.latency,
                    EnumWrappers.NativeGameMode.fromBukkit(entry.gameMode), WrappedChatComponent.fromText(entry.name)));
        }
        packet.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        packet.setData(players);
        packet.broadcastPacket();
    }

    public void testSendPacket() {
        // Хедер и футер
        // Отправляет пакет, который добавляет таблисту хедер "BeuBass" и футер "Anal"

        WrapperPlayServerPlayerListHeaderFooter packetHeaderFooter = new WrapperPlayServerPlayerListHeaderFooter();
        packetHeaderFooter.setFooter(getStringAsWrappedChatComponent("BeuBass"));
        packetHeaderFooter.setHeader(getStringAsWrappedChatComponent("Anal"));
        packetHeaderFooter.broadcastPacket();

        // Сам список игроков
        // Добавляет в список игроков игрока с ником "Danbonus"

        WrapperPlayServerPlayerInfo packetInfo = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();

        players.add(new PlayerInfoData(new WrappedGameProfile(UUID.randomUUID(), "Danbonus"), -1, EnumWrappers.NativeGameMode.SURVIVAL, getStringAsWrappedChatComponent("Danbonus")));

        packetInfo.setAction(EnumWrappers.PlayerInfoAction.ADD_PLAYER);
        packetInfo.setData(players);
        packetInfo.broadcastPacket();

        /*
        P.S. для отправки конкретному игроку юзать packet.sendPacket(Player player).
         */
    }

    private WrappedChatComponent getStringAsWrappedChatComponent(String text) {
        return WrappedChatComponent.fromJson(ComponentSerializer.toString(new TextComponent(text)));
    }

    // ================== Для таска ================= //

    void clearEntries() {
        this.entries.clear();
    }

    void sortEntries() {
        HashSet<WrappedTabListEntry> copyEntries = new HashSet<>(this.entries);
        LinkedHashSet<WrappedTabListEntry> out = new LinkedHashSet<>();
        HashSet<WrappedTabListEntry> ungroupped = new HashSet<>();

        this.entries.clear();

        for (String group : PluginConfig.tabSortGroups) {
            for (WrappedTabListEntry entry : copyEntries) {
                if (out.contains(entry)) continue;

                if (entry.permissionUser.inGroup(group, false)) {
                    out.add(entry);
                } else //noinspection deprecation заебало
                    if (entry.permissionUser.getAllGroups().size() < 1) {
                    ungroupped.add(entry);
                }
            }
        }

        out.addAll(ungroupped);

        this.entries = out;
    }
}
