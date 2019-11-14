package ru.allformine.afmcp.vanish;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabList;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class VanishTabList {
    public Set<String> tabList = new HashSet<>();

    public void addTabListPlayer(String nickname) {
        if (tabList.add(nickname)) updateTabList(); // Если добавилось - обновляем список.
    }

    public void removeTabListPlayer(String nickname) {
        if (tabList.remove(nickname)) updateTabList(); // Если удалилось - обновляем список.

    }

    // ==================== //

    private void updateTabList() {
        Sponge.getServer().getOnlinePlayers().forEach(player -> {
            TabList playerTabList = player.getTabList();
            Collection<TabListEntry> entriesCopy = new ArrayList<>(playerTabList.getEntries());

            entriesCopy.forEach(entry -> playerTabList.removeEntry(entry.getProfile().getUniqueId())); // Удаление всех записей
            tabList.forEach(nickname -> playerTabList.addEntry(getTabListEntryForPlayer(nickname, playerTabList)));
        });
    }

    private static TabListEntry getTabListEntryForPlayer(String nickname, TabList list) {
        Player player = Sponge.getServer().getPlayer(nickname).get();

        return TabListEntry.builder()
                .list(list)
                .gameMode(player.gameMode().get())
                .profile(player.getProfile())
                .latency(player.getConnection().getLatency())
                .displayName(Text.of(player.getName()))
                .build();
    }
}
