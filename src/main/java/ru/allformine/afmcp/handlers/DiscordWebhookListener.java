package ru.allformine.afmcp.handlers;

import br.net.fabiozumbi12.UltimateChat.Sponge.API.SendChannelMessageEvent;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.advancement.AdvancementEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.net.api.Webhook;

public class DiscordWebhookListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        /*Discord.sendMessagePlayer(event.getTargetEntity().hasPlayedBefore() ?
                Discord.MessageTypePlayer.TYPE_PLAYER_JOINED :
                Discord.MessageTypePlayer.TYPE_PLAYER_JOINED_FIRST_TIME,
                "",
                event.getTargetEntity());*/
        Webhook.TypePlayerMessage type = event.getTargetEntity().hasPlayedBefore() ?
                Webhook.TypePlayerMessage.JOINED_SERVER :
                Webhook.TypePlayerMessage.JOINED_FIRST_TIME;
        Webhook.sendPlayerMessage(type, event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        //Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_LEFT, "", event.getTargetEntity());
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LEFT_SERVER, event.getTargetEntity());
    }

    @Listener
    public void onSendChannelMessageEvent(SendChannelMessageEvent event) {
        if (event.getSender() instanceof Player) {
            /*Discord.sendMessagePlayer(!event.getChannel().getName().equals("Local") ?
                    Discord.MessageTypePlayer.TYPE_PLAYER_CHAT :
                    Discord.MessageTypePlayer.TYPE_PLAYER_CHAT_LVL2,
                    String.format("[%s] %s", event.getChannel().getName(),
                            event.getMessage().toPlain()
                    ),
                    (Player) event.getSender()
            );*/
            String channelName = event.getChannel().getName();
            Player sender = (Player) event.getSender();
            String message = event.getMessage().toPlain();
            if (channelName.equalsIgnoreCase("Local")) {
                int x = (int) sender.getLocation().getX();
                int y = (int) sender.getLocation().getY();
                int z = (int) sender.getLocation().getZ();
                Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LVL2_CHAT_MESSAGE,
                        sender,
                        String.format("X: %s, Y: %s, Z: %s", x, y, z),
                        channelName,
                        message
                );
            } else {
                Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.CHAT_MESSAGE, sender, channelName, message);
            }
        }
    }

    @Listener
    public void onCommandSend(SendCommandEvent event, @First Player player) {
        //Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_COMMAND, event.getCommand() + " " + event.getArguments(), p);
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.COMMAND, player, event.getCommand() + " " + event.getArguments());
    }

    @Listener
    public void onAdvancement(AdvancementEvent.Grant event) {
        if (!event.getAdvancement().getName().startsWith("recipes_")) {
            /*Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_EARNED_ADVANCEMENT,
                    event.getAdvancement().getName(),
                    event.getTargetEntity());*/
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.EARNED_ADVANCEMENT,
                    event.getTargetEntity(),
                    event.getAdvancement().getName()
            );
        }
    }

    @Listener
    public void onDeath(DestructEntityEvent.Death event) {
        if (event.getTargetEntity() instanceof Player) {
            /*Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_DIED,
                    event.getMessage().toPlain(),
                    (Player) event.getTargetEntity());*/
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.DIED,
                    (Player) event.getTargetEntity(),
                    event.getMessage().toPlain()
            );
        }
    }
}
