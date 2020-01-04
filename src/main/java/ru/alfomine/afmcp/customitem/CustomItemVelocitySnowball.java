package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public class CustomItemVelocitySnowball extends CustomItem {
    @Override
    public String getId() {
        return "vc_snowball";
    }

    @Override
    public String getName() {
        return "Velocity Snowball";
    }

    @Override
    public Material getMaterial() {
        return Material.SNOW_BALL;
    }

    @Override
    public void onUse(Player player) {
        player.sendMessage("Beu");
    }
}
