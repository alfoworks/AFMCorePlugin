package ru.allformine.afmcp;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.network.play.server.SPacketPlayerListItem;

public class VanishPacketListener extends PacketListenerAdapter {
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        if (!(event.getPacket() instanceof SPacketPlayerListItem)) {
            event.setCancelled(true);
        }
    }
}
