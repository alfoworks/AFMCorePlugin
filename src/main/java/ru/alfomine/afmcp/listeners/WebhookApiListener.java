package ru.alfomine.afmcp.listeners;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import ru.alfomine.afmcp.PluginConfig;
import ru.alfomine.afmcp.webhookapi.MessageTypePlayer;
import ru.alfomine.afmcp.webhookapi.WebhookApi;

public class WebhookApiListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        WebhookApi.sendPlayerMessage(event.getPlayer().hasPlayedBefore() ? MessageTypePlayer.JOINED_SERVER : MessageTypePlayer.JOINED_FIRST_TIME, event.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        WebhookApi.sendPlayerMessage(MessageTypePlayer.LEFT_SERVER, event.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onChat(ChannelChatEvent event) {
        if (isChannelLocal(event.getChannel())) {
            String coordsString = locationToString(event.getSender().getPlayer().getLocation());

            if (isDM(event.getChannel())) {
                WebhookApi.sendPlayerMessage(MessageTypePlayer.LVL2_CHAT_MESSAGE, event.getSender().getPlayer().getDisplayName(), coordsString, "ЛС", "ЛС пока что не поддерживается, хуй.");
            } else {
                WebhookApi.sendPlayerMessage(MessageTypePlayer.LVL2_CHAT_MESSAGE, event.getSender().getPlayer().getDisplayName(), coordsString, event.getChannel().getName(), event.getMessage());
            }
        } else {
            WebhookApi.sendPlayerMessage(MessageTypePlayer.CHAT_MESSAGE, event.getSender().getPlayer().getDisplayName(), event.getChannel().getName(), event.getMessage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        String text = event.getMessage();

        for (String cmd : PluginConfig.hiddenCommandsList) {
            if (text.startsWith("/" + cmd)) {
                text = "Команда скрыта!";
                break;
            }
        }

        WebhookApi.sendPlayerMessage(MessageTypePlayer.COMMAND, event.getPlayer().getDisplayName(), text);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event) {
        WebhookApi.sendPlayerMessage(MessageTypePlayer.DIED, event.getEntity().getDisplayName(), event.getDeathMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        WebhookApi.sendPlayerMessage(MessageTypePlayer.EARNED_ADVANCEMENT, event.getPlayer().getDisplayName(), event.getAdvancement().getKey().getKey());
    }

    // ================================ //
    private boolean isChannelLocal(Channel channel) {
        return !channel.isCrossWorld() && channel.getDistance() != 0 || channel.getName().startsWith("convo");
    }

    private boolean isDM(Channel channel) {
        return channel.getName().startsWith("convo");
    }

    private String locationToString(Location location) {
        String locationStrings[] = {String.valueOf(Math.round(location.getX())), String.valueOf(Math.round(location.getY())), String.valueOf(Math.round(location.getZ()))};

        return String.join(", ", locationStrings);
    }
}
