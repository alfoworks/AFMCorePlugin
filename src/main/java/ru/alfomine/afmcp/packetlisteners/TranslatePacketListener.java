package ru.alfomine.afmcp.packetlisteners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
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
            String message = ComponentSerializer.parse(event.getPacket().getChatComponents().read(0).getJson())[0].toPlainText();
            String retranslated = YandexTranslator.retranslate(message);

            PacketContainer packet = event.getPacket();

            WrappedChatComponent component = WrappedChatComponent.fromJson(ComponentSerializer.toString(new TextComponent(retranslated)));
            packet.getChatComponents().write(0, component);

            event.setPacket(packet);
        }
    }
}
