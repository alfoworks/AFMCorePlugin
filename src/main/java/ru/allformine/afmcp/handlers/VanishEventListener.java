package ru.allformine.afmcp.handlers;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishEventListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();
        if (player.hasPermission(VanishManager.vanishPermission)) {
            VanishManager.vanishPlayer(player, true);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Player player = event.getTargetEntity();
        if (player.hasPermission(VanishManager.vanishPermission)) {
            VanishManager.unvanishPlayer(player, true);
        }
    }
}
