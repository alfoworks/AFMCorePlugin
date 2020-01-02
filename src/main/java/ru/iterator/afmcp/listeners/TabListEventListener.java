package ru.iterator.afmcp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.iterator.afmcp.AFMCorePlugin;

public class TabListEventListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AFMCorePlugin.tabList.addEntry(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        AFMCorePlugin.tabList.removeEntry(event.getPlayer(), true);
    }
}
