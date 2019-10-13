package ru.allformine.afmcp;

import eu.crushedpixel.sponge.packetgate.api.event.PacketEvent;
import eu.crushedpixel.sponge.packetgate.api.listener.PacketListenerAdapter;
import eu.crushedpixel.sponge.packetgate.api.registry.PacketConnection;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import org.spongepowered.api.Sponge;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishPacketListener extends PacketListenerAdapter {
    @Override
    public void onPacketWrite(PacketEvent event, PacketConnection connection) {
        if (!(event.getPacket() instanceof SPacketPlayerListItem)) {
            return;
        }

        SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();

        for (SPacketPlayerListItem.AddPlayerData crap : packet.getEntries()) {
            if (!VanishManager.isVanished(Sponge.getServer().getPlayer(crap.getProfile().getName()).get())) {
                event.setCancelled(true);
                return;
            }
        }
    }
}
