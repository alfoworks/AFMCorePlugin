package ru.allformine.afmcp;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.network.ChannelBuf;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class Messaging {
    public static void sendMessage(@Nullable Player player, String message, MessageType type) {
        Consumer<ChannelBuf> buffer = channelBuf -> {
            channelBuf.writeInteger(type.typeInt);
            channelBuf.writeString(message);
        };

        if (player == null) {
            PacketChannels.MESSAGING.sendToAll(buffer);
        } else {
            PacketChannels.MESSAGING.sendTo(player, buffer);
        }
    }

    public enum MessageType {
        NOTIFY(0),
        WINDOWED(1);

        private int typeInt;

        MessageType(int typeInt) {
            this.typeInt = typeInt;
        }
    }
}
