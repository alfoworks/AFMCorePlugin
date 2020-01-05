package ru.alfomine.afmcp.customitem;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class CustomItemVelocitySnowball extends CustomItem {
    private HashMap<Player, Integer> velocityPlayers = new HashMap<>();

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
        if (player.getWorld().getBlockAt(player.getLocation().subtract(0, 1, 0)).getType() == Material.AIR || player.isFlying() || player.isGliding()) {
            return;
        }

        player.setVelocity(player.getVelocity().add(new Vector(0, 150, 0)));
        velocityPlayers.put(player, player.getLocation().getBlockY());
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!velocityPlayers.containsKey(event.getPlayer()) || event.getPlayer().getVelocity() == new Vector()) {
            return;
        }

        if (event.getTo().getBlockY() > velocityPlayers.get(event.getPlayer())) {
            velocityPlayers.put(event.getPlayer(), event.getTo().getBlockY());
            return;
        }

        event.getPlayer().setGliding(true);

        velocityPlayers.remove(event.getPlayer());
    }
}
