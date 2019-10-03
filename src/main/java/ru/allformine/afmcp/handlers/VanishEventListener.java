package ru.allformine.afmcp.handlers;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishEventListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.vanishPlayer(event.getTargetEntity(), true);
        }
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.unvanishPlayer(event.getTargetEntity(), true);
        }
    }
}
