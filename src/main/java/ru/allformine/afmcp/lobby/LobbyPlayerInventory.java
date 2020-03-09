package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.property.SlotIndex;

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

        Inventory inventory = player.getInventory();
        Hotbar hotbar = inventory.query(Hotbar.class);
        hotbar.set(new SlotIndex(slutIndex), item.getAsItemStack());
    }
}

