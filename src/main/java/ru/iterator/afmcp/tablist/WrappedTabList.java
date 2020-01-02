package ru.iterator.afmcp.tablist;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.iterator.afmcp.PluginConfig;
import ru.iterator.afmcp.AFMCorePlugin;

import java.util.*;

public class WrappedTabList {
    private LinkedHashSet<WrappedTabListEntry> entries; //Linked потому, что требуется сортировка.

    public WrappedTabList() {
        this.entries = new LinkedHashSet<>();
        Bukkit.getScheduler().runTaskTimer(AFMCorePlugin.getPlugin(), new TabListUpdateTask(this), 0, 100);
    }

    // boolean sort - сортировать только в случае добавления игроков из ивента.
    // В таске игроки добавляются последовательно, а значит что лишняя сортировка создаст лишнюю нагрузку.

    public void addEntry(Player player, boolean sort) {
        this.entries.add(new WrappedTabListEntry(player));

        if (sort) sortEntries();
    }

    public void removeEntry(Player player, boolean sort) {
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

        if (sort) sortEntries();
    }

    public LinkedHashSet<WrappedTabListEntry> getEntries() { // Дает Immutable лист (неизменяемый)
        return this.entries;
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
                } else if (entry.permissionUser.getAllGroups().size() < 1) {
                    ungroupped.add(entry);
                }
            }
        }

        out.addAll(ungroupped);

        this.entries = out;
    }
}
