package ru.alfomine.afmcp.lobby;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class LobbyPlayerInventory {
    Set<LobbyItem> items = new HashSet<>();
    Player player;

    public LobbyPlayerInventory(Player player) {
        this.player = player;
    }

    public void addItem(LobbyItem item, int slutIndex) {
        item.slutIndex = slutIndex;
        this.items.add(item);

        this.player.getInventory().setItem(slutIndex, item.getAsItemStack());
    }
}
