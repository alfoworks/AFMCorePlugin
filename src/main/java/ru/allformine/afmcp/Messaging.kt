package ru.allformine.afmcp

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.network.ChannelBuf
import java.util.function.Consumer

object Messaging {
    @JvmStatic
    fun sendMessage(player: Player?, message: String, type: MessageType) {
        val buffer = Consumer { channelBuf: ChannelBuf ->
            channelBuf.writeString(message)
        }

        if (player == null) {
            PacketChannels.MESSAGING.sendToAll(buffer)
        } else {
            PacketChannels.MESSAGING.sendTo(player, buffer)
        }
    }

    enum class MessageType(val typeInt: Int) {
        NOTIFY(0),
        WINDOWED(1);
    }
}