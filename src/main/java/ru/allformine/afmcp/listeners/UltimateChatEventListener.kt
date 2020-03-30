package ru.allformine.afmcp.listeners

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import ru.allformine.afmcp.net.api.Webhook

class UltimateChatEventListener {
    private var publicChannels = listOf("Global", "Trade")

    @Listener(order = Order.POST)
    fun onSendChannelMessageEvent(event: SendChannelMessageEvent) {
        val channelName = event.channel.name
        val sender = event.sender
        val message = event.message.toPlain()

        if (!publicChannels.contains(channelName)) {
            val location: String = if (sender is Player) {
                String.format("X: %s, Y: %s, Z: %s", sender.location.blockX, sender.location.blockY, sender.location.blockZ)
            } else {
                "No location"
            }

            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LVL2_CHAT_MESSAGE,
                    sender,
                    location,
                    channelName,
                    message
            )
        } else {
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.CHAT_MESSAGE, sender, channelName, message)
        }
    }
}