package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class CustomItem implements Listener {
    public String getId() {
        return "123";
    }

    public String getName() {
        return "123";
    }

    public Material getMaterial() {
        return Material.ACACIA_DOOR;
    }

    public void onUse(Player player) {

    }
}
