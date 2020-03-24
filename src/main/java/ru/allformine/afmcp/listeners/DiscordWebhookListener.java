package ru.allformine.afmcp.listeners;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.advancement.AdvancementEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.api.Webhook;

public class DiscordWebhookListener {
    @Listener(order = Order.POST)
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Webhook.TypePlayerMessage type = !event.isMessageCancelled() ? (event.getTargetEntity().hasPlayedBefore() ?
                Webhook.TypePlayerMessage.JOINED_SERVER :
                Webhook.TypePlayerMessage.JOINED_FIRST_TIME) : Webhook.TypePlayerMessage.STAFF_JOINED_SERVER;
        Webhook.sendPlayerMessage(type, event.getTargetEntity());
    }

    @Listener(order = Order.POST)
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        if (AFMCorePlugin.serverRestart) return;
        Webhook.sendPlayerMessage(!event.isMessageCancelled() ? Webhook.TypePlayerMessage.LEFT_SERVER : Webhook.TypePlayerMessage.STAFF_LEFT_SERVER, event.getTargetEntity());
    }

    @Listener(order = Order.POST)
    public void onSendChannelMessageEvent(SendChannelMessageEvent event) {
        if (event.getSender() instanceof Player) {
            String channelName = event.getChannel().getName();
            Player sender = (Player) event.getSender();
            String message = event.getMessage().toPlain();
            if (!channelName.equals("Global") && !channelName.equals("Trade")) {
                Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LVL2_CHAT_MESSAGE,
                        sender,
                        sender.getLocation().toString(),
                        channelName,
                        message
                );
            } else {
                Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.CHAT_MESSAGE, sender, channelName, message);
            }
        }
    }

    @Listener(order = Order.POST)
    public void onCommandSend(SendCommandEvent event, @First Player player) {
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.COMMAND, player, event.getCommand() + " " + event.getArguments());
    }

    @Listener(order = Order.POST)
    public void onAdvancement(AdvancementEvent.Grant event) {
        if (!event.getAdvancement().getName().startsWith("recipes_")) {
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.EARNED_ADVANCEMENT,
                    event.getTargetEntity(),
                    event.getAdvancement().getName()
            );
        }
    }

    @Listener(order = Order.POST)
    public void onDeath(DestructEntityEvent.Death event) {
        if (event.getTargetEntity() instanceof Player) {
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.DIED,
                    (Player) event.getTargetEntity(),
                    event.getMessage().toPlain()
            );
        }
    }
}