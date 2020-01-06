package ru.alfomine.afmcp.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alfomine.afmcp.net.YandexTranslator;

public class ShitEvenListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        String retranslated = YandexTranslator.retranslate(event.getMessage());

        if (retranslated != null) event.setMessage(retranslated);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        String retranslated = YandexTranslator.retranslate(event.getDeathMessage());

        if (retranslated != null) event.setDeathMessage(retranslated);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        String retranslated = YandexTranslator.retranslate(event.getJoinMessage());

        if (retranslated != null) event.setJoinMessage(retranslated);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        String retranslated = YandexTranslator.retranslate(event.getQuitMessage());

        if (retranslated != null) event.setQuitMessage(retranslated);
    }
}
