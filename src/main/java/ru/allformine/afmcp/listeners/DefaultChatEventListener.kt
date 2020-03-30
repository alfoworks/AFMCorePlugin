package ru.allformine.afmcp.listeners

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.message.MessageChannelEvent
import ru.allformine.afmcp.net.api.Webhook

class DefaultChatEventListener {
    @Listener(order = Order.POST)
    fun onChat(e: MessageChannelEvent.Chat, @First p: Player) {
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.CHAT_MESSAGE, p, "", e.message.toPlain())
    }
}