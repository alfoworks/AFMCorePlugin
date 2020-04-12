package ru.allformine.afmcp.listeners;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.util.Optional;

public class ChatCorrectionListener {
    @Listener(order = Order.EARLY)
    public void onChannelMessage(SendChannelMessageEvent event) {
        Text message = event.getMessage();

        message = Text.of(message.toPlain());
        message = processMentions(processCommand(message));

        event.setMessage(processMentions(processCommand(message)));
    }

    private Text processMentions(Text message) {
        String messageString = TextSerializers.FORMATTING_CODE.serialize(message);
        String[] messagesSplit = messageString.split(" ");
        StringBuilder newMessage = new StringBuilder();

        for (String word : messagesSplit) {
            if (word.startsWith("@")) {
                Optional<Player> player = Sponge.getServer().getPlayer(word.substring(1));

                if (player.isPresent()) {
                    word = String.format("&e&l@%s", player.get().getName());

                    player.get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, player.get().getPosition(), 2);
                    player.get().playSound(SoundTypes.ENTITY_ARROW_HIT, player.get().getPosition(), 2, 0);
                }
            }

            newMessage.append(word);
            newMessage.append(" ");
        }

        return TextSerializers.FORMATTING_CODE.deserialize(newMessage.toString());
    }

    private Text processCommand(Text message) {
        String msgString = TextSerializers.FORMATTING_CODE.serialize(message);

        return msgString.startsWith("./") ? Text.of(msgString.substring(1)) : message;
    }
}
