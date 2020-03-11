package ru.allformine.afmcp.listeners;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class ChatEventListener {
    @Listener(order = Order.EARLY)
    public void onChannelMessage(SendChannelMessageEvent event) {
        if (!(event.getSender() instanceof Player)) {
            return;
        }

        String[] messageSplit = event.getMessage().toString().split(" ");
        Text.Builder newMessage = Text.builder();

        for (String message : messageSplit) {
            if (message.startsWith("@")) {
                Optional<Player> protoPlayer = Sponge.getServer().getPlayer(message.substring(1));

                if (protoPlayer.isPresent()) {
                    newMessage.append(Text.builder().append(Text.of(String.format("@%s", protoPlayer.get().getName()))).color(TextColors.YELLOW).build());

                    protoPlayer.get().playSound(SoundTypes.ENTITY_EXPERIENCE_ORB_PICKUP, protoPlayer.get().getPosition(), 1);
                    protoPlayer.get().playSound(SoundTypes.ENTITY_ARROW_HIT, protoPlayer.get().getPosition(), 1);

                    break;
                }
            }

            newMessage.append(Text.of(message));
        }

        event.setMessage(newMessage.build());
    }
}
