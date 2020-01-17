package ru.alfomine.afmcp.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerInfo;
import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.*;
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

    public void sendPlayerInfo() {
        for (WrappedTabListEntry entry : this.entries) {
            WrapperPlayServerPlayerListHeaderFooter packet = new WrapperPlayServerPlayerListHeaderFooter();
            packet.setFooter(getStringAsWrappedChatComponent(entry.footer));
            packet.setHeader(getStringAsWrappedChatComponent(entry.header));
            packet.sendPacket(Bukkit.getPlayer(entry.uuid));
        }
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

    public void flush() {
        WrapperPlayServerPlayerInfo packet = new WrapperPlayServerPlayerInfo();
        List<PlayerInfoData> players = new ArrayList<>();
        for (WrappedTabListEntry entry : this.entries) {
            players.add(new PlayerInfoData(
                    new WrappedGameProfile(entry.uuid, entry.permissionUser.getName()), entry.latency,
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
        // return WrappedChatComponent.fromJson(ComponentSerializer.toString(new TextComponent(text)));
        return WrappedChatComponent.fromText(text);
    }

    // ================== Для таска ================= //

    void clearEntries() {
        this.entries.clear();
    }

    void sortEntries(@SuppressWarnings("SameParameterValue") int mode) {
        if (mode == 3) { // Экспериментальный способ 2
            this.entries.sort((a, b) -> {
                String aName = a.permissionUser.getIdentifier();
                String bName = b.permissionUser.getIdentifier();
                List<String> priority = Arrays.asList(PluginConfig.tabSortGroups);
                AFMCorePlugin.log("a " + aName + "; b " + bName, Level.INFO);
                return Integer.compare(priority.indexOf(aName), priority.indexOf(bName));
            });
        } else if (mode == 2) {// Экспериментальный способ 1 ревизия 1
            this.entries.sort((a, b) -> {
                String aName = a.permissionUser.getParentIdentifiers(null)
                        .get(a.permissionUser.getParentIdentifiers(null).size()-1);
                String bName = b.permissionUser.getParentIdentifiers(null)
                        .get(b.permissionUser.getParentIdentifiers(null).size()-1);
                List<String> priority = Arrays.asList(PluginConfig.tabSortGroups);
                AFMCorePlugin.log("a " + aName + "; b " + bName, Level.INFO);
                return Integer.compare(priority.indexOf(aName), priority.indexOf(bName));
            });
        } else if (mode == 1) {// Экспериментальный способ 1 ревизия 0
            this.entries.sort((a, b) -> {
                String aName = a.permissionUser.getParentIdentifiers(null)
                        .get(0); // .get(a.permissionUser.getParentIdentifiers(null).size()-1);
                String bName = b.permissionUser.getParentIdentifiers(null)
                        .get(0); // .get(b.permissionUser.getParentIdentifiers(null).size()-1);
                List<String> priority = Arrays.asList(PluginConfig.tabSortGroups);
                AFMCorePlugin.log("a " + aName + "; b " + bName, Level.INFO);
                return Integer.compare(priority.indexOf(aName), priority.indexOf(bName));
            });
        } else { // Нормальный способ
            // TODO Сложность алгоритма квадратична, нужно оптимизировать. Занимает много памяти.
            ArrayList<WrappedTabListEntry> newEntries = new ArrayList<>();
            ArrayList<WrappedTabListEntry> queue = new ArrayList<>(this.entries);
            queue.sort(Comparator.comparing(a -> a.permissionUser.getName()));
            for (String group : PluginConfig.tabSortGroups) {
                for (WrappedTabListEntry entry : queue) {
                    if (entry.permissionUser.inGroup(group, false)) {
                        queue.remove(entry);
                        newEntries.add(entry);
                    }
                }
            }
            newEntries.addAll(queue);
            this.entries = newEntries;
        }
    }
}
