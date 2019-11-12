package ru.allformine.afmcp.vanish;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.api.Sponge;

import java.lang.reflect.Method;
import java.util.List;

public class VanishPacketListener extends PacketListenerAdapter {
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        if (!(event.getPacket() instanceof SPacketPlayerListItem)) {
            return;
        }

        SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

        try {
            for (Method method : packet.getClass().getDeclaredMethods()) {
                if (method.toString().contains("getPlayerDatas")) {
                    ((List<SPacketPlayerListItem.AddPlayerData>) method.invoke(packet)).forEach(entry -> {
                        if (VanishManager.playersToRemove.contains(entry.getProfile().getName())) {
                            event.setCancelled(true);
                        } else {
                            Sponge.getServer().getPlayer(entry.getProfile().getName()).ifPresent(player -> {
                                if (VanishManager.isVanished(player)) {
                                    event.setCancelled(true);
                                }
                            });
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
