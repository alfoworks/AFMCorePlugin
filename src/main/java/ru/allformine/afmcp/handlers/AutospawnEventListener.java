package ru.allformine.afmcp.handlers;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;

public class AutospawnEventListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
//        if (!event.getTargetEntity().hasPlayedBefore()) {
//            Sponge.getCommandManager().process(Sponge.getServer().getConsole(),
//                    "spawn other " + event.getTargetEntity().getName());
//        }
    }
}
