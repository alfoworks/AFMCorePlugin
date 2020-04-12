package ru.allformine.afmcp.listeners

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent.Disconnect
import org.spongepowered.api.event.network.ClientConnectionEvent.Join
import org.spongepowered.api.text.serializer.TextSerializers
import ru.allformine.afmcp.Messaging
import ru.allformine.afmcp.Messaging.sendMessage

class JoinQuitMessageListener {
    @Listener
    fun onPlayerJoin(event: Join) {
        if (!event.targetEntity.hasPlayedBefore()) {
            sendMessage(event.targetEntity, "Привет! Добро пожаловать на ALFO:MINE. Мы рады видеть новых игроков. Если у тебя появятся вопросы, задай их нам на http://mine.alfo.ws/support/", Messaging.MessageType.WINDOWED)
        } else {
            sendMessage(event.targetEntity, "Добро пожаловать на ALFO:MINE Litefactor (если заебут эти сообщения при каждом входе - скажите)", Messaging.MessageType.WINDOWED)
        }

        event.setMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&7-&2G &7%s &aвошёл в игру!", event.targetEntity.name)))
    }

    @Listener
    fun onPlayerQuit(event: Disconnect) {
        event.setMessage(TextSerializers.FORMATTING_CODE.deserialize(String.format("&7-&2G &7%s &cвышел из игры!", event.targetEntity.name)))
    }
}