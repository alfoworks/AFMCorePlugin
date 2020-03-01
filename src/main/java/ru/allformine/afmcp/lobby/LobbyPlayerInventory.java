package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

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

        Inventory main = player.getInventory().query(QueryOperationTypes.INVENTORY_PROPERTY);
        ((MainPlayerInventory) main).getGrid().getSlot(0, slutIndex).ifPresent(slut -> {
            slut.set(item.getAsItemStack());
        });
    }
}

