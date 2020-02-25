package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.property.SlotPos;

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

        // TODO
        //this.player.getInventory().query(SlotPos.of()).
        //this.player.getInventory().setItem(slutIndex, item.getAsItemStack());
    }
}

