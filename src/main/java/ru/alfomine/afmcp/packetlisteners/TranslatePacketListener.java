package ru.alfomine.afmcp.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.plugin.Plugin;
import ru.alfomine.afmcp.PluginStatics;
import ru.alfomine.afmcp.net.YandexTranslator;

public class TranslatePacketListener extends PacketAdapter {
    public TranslatePacketListener(Plugin plugin, PacketType... types) {
        super(plugin, types);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        if (!PluginStatics.debugRetranslateEnabled) return;

        if (event.getPacketType() == PacketType.Play.Server.CHAT) {
            String retranslated = YandexTranslator.retranslate(event.getPacket().getStrings().read(0));

            event.setCancelled(true);

            event.getPlayer().sendMessage(retranslated);
        }
    }
}
