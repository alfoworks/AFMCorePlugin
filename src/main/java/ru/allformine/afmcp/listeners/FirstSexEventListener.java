package ru.allformine.afmcp.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.Messaging;

public class FirstSexEventListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!event.getTargetEntity().hasPlayedBefore()) {
            Messaging.sendMessage(event.getTargetEntity(), "Привет! Добро пожаловать на ALFO:MINE. Мы рады тебя видеть.\nсосни хуй", Messaging.MessageType.WINDOWED);
        }
    }
}
