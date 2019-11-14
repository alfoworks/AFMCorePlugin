package ru.allformine.afmcp.vanish;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.network.play.server.SPacketPlayerListItem;

import java.lang.reflect.Method;
import java.util.List;

public class VanishPacketListener extends PacketListenerAdapter {
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        try {
            if (!(event.getPacket() instanceof SPacketPlayerListItem)) {
                return;
            }

            SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

            for (Method method : packet.getClass().getDeclaredMethods()) {
                if (method.toString().contains("getAction")) {
                    if (method.invoke(packet) == SPacketPlayerListItem.Action.REMOVE_PLAYER) {
                        return;
                    }
                }
            }

            for (Method method : packet.getClass().getDeclaredMethods()) {
                if (method.toString().contains("getPlayerDatas")) {
                    ((List<SPacketPlayerListItem.AddPlayerData>) method.invoke(packet)).forEach(entry -> {
                        if (!VanishManager.tabList.tabList.contains(entry.getProfile().getName())) {
                            event.setCancelled(true);
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
