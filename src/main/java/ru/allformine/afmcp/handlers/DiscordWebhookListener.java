package ru.allformine.afmcp.handlers;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.advancement.AdvancementEvent;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.net.discord.Discord;

public class DiscordWebhookListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Discord.sendMessagePlayer(event.getTargetEntity().hasPlayedBefore() ? Discord.MessageTypePlayer.TYPE_PLAYER_JOINED : Discord.MessageTypePlayer.TYPE_PLAYER_JOINED_FIRST_TIME, "", event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_LEFT, "", event.getTargetEntity());
    }

    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player p /*нихуя не понимаю, че это за нахуй ебать*/) {
        Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_CHAT, event.getRawMessage().toPlain(), p);
    }

    @Listener
    public void onCommandSend(SendCommandEvent event, @First Player p) {
        Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_COMMAND, event.getCommand()+" "+event.getArguments(), p);
    }

    @Listener
    public void onAdvancement(AdvancementEvent event) {
        Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_EARNED_ADVANCEMENT, event.getAdvancement().getName(), event.getTargetEntity());
    }

    @Listener
    public void onDeath(DestructEntityEvent.Death event) {
        if(event.getTargetEntity() instanceof Player) {
            Discord.sendMessagePlayer(Discord.MessageTypePlayer.TYPE_PLAYER_DIED, event.getMessage().toPlain(), (Player) event.getTargetEntity());
        }
    }
}
