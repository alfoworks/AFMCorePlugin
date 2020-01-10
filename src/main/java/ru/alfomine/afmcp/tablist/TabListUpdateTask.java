package ru.alfomine.afmcp.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TabListUpdateTask implements Runnable {
    private WrappedTabList tabList;

    TabListUpdateTask(WrappedTabList tabList) {
        this.tabList = tabList;
    }

    @Override
    public void run() {
        this.tabList.clearEntries();

        for (Player player : Bukkit.getOnlinePlayers()) {
            this.tabList.addEntry(player);
        }

        this.tabList.sortEntries();
        this.tabList.sendPlayerInfo();

    }
}
