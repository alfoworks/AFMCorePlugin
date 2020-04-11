package ru.allformine.afmcp.listeners

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent.Join
import ru.allformine.afmcp.Messaging
import ru.allformine.afmcp.Messaging.sendMessage

class MessagingEventListener {
    @Listener
    fun onPlayerJoin(event: Join) {
        if (!event.targetEntity.hasPlayedBefore()) {
            sendMessage(event.targetEntity, "Привет! Добро пожаловать на ALFO:MINE. Мы рады тебя видеть.\nсосни хуй", Messaging.MessageType.WINDOWED)
        }
    }
}