package ru.allformine.afmcp.net.discord;

import net.minecraft.util.com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.References;
import ru.allformine.afmcp.net.http.Requests;

import java.util.HashMap;
import java.util.Map;

public class Discord {
    public enum MessageTypeServer {
        TYPE_SERVER_STARTED,
        TYPE_SERVER_STOPPED,

        TPS_IS_BAD,
        TPS_NORMALIZED
    }

    public enum MessageTypePlayer {
        TYPE_PLAYER_JOINED,
        TYPE_PLAYER_JOINED_FIRST_TIME,
        TYPE_PLAYER_LEFT,
        TYPE_STAFF_JOINED,
        TYPE_STAFF_LEFT,
        TYPE_PLAYER_DIED,
        TYPE_PLAYER_EARNED_ACHIEVEMENT,
        TYPE_PLAYER_CHAT,
        TYPE_PLAYER_CHAT_LVL2,
        TYPE_PLAYER_COMMAND
    }

    public static void sendMessage(String text, String username, String avatarUrl, int logLevel) {
        Plugin plugin = AFMCorePlugin.getPlugin();

        if (!References.log) {
            return;
        }

        String url = plugin.getConfig().getString("discord.webhooks.url_lvl" + String.valueOf(logLevel));

        Map<String, String> JSON = new HashMap<>();
        JSON.put("content", text);

        if (avatarUrl != null) {
            JSON.put("avatar_url", plugin.getConfig().getString("discord.webhooks.player_avatar_url") + username);
        }
        if (username != null) {
            JSON.put("username", username);
        }

        String JSONString = new Gson().toJson(JSON);

        Requests.sendPost(JSONString, url);
    }

    public static void sendMessageServer(MessageTypeServer type) {
        String text;

        if (type == MessageTypeServer.TYPE_SERVER_STARTED) {
            text = "✅ Сервер поднялся!";
        } else if (type == MessageTypeServer.TYPE_SERVER_STOPPED) {
            text = "❌ Сервер упал! (<@&394132635791654913>)";
        } else if (type == MessageTypeServer.TPS_IS_BAD) {
            text = "\uD83D\uDCC9 TPS опустился ниже 14! (<@&394132635791654913>)";
        } else if (type == MessageTypeServer.TPS_NORMALIZED) {
            text = "\uD83D\uDCC8 TPS вернулся в норму.";
        } else {
            throw new IllegalArgumentException();
        }

        sendMessage(text, "Сервер", null, 1);
    }

    public static void sendMessagePlayer(MessageTypePlayer type, String add, Player player) {
        String text;
        int logLevel = 1;

        if (type == MessageTypePlayer.TYPE_PLAYER_JOINED) {
            text = "➡ Вошел в игру.";
        } else if (type == MessageTypePlayer.TYPE_PLAYER_JOINED_FIRST_TIME) {
            text = "\uD83D\uDD30➡ Вошел в игру впервые!";
        } else if (type == MessageTypePlayer.TYPE_PLAYER_LEFT) {
            text = "⬅ Вышел из игры.";
        } else if (type == MessageTypePlayer.TYPE_STAFF_JOINED) {
            text = "➡ Вошел в игру (персонал).";
            logLevel = 2;
        } else if (type == MessageTypePlayer.TYPE_STAFF_LEFT) {
            text = "➡ Вышел из игры (персонал).";
            logLevel = 2;
        } else if (type == MessageTypePlayer.TYPE_PLAYER_DIED) {
            text = "☠ " + add + ".";
        } else if (type == MessageTypePlayer.TYPE_PLAYER_EARNED_ACHIEVEMENT) {
            text = "\uD83C\uDFC6 получил достижение **" + add + "**.";
        } else if (type == MessageTypePlayer.TYPE_PLAYER_CHAT) {
            text = "`" + add + "`";
        } else if (type == MessageTypePlayer.TYPE_PLAYER_CHAT_LVL2) {
            text = "`" + add + "`";
            logLevel = 2;
        } else if (type == MessageTypePlayer.TYPE_PLAYER_COMMAND) {
            text = "\uD83D\uDCA0 выполнил команду **" + add + "**.";
            logLevel = 2;
        } else {
            throw new IllegalArgumentException();
        }

        int finalLogLevel = logLevel; // костыль от идеи, спасибо, идея!
        Bukkit.getScheduler().runTaskAsynchronously(AFMCorePlugin.getPlugin(), () -> sendMessage(text, player.getDisplayName(), AFMCorePlugin.getPlugin().getConfig().getString("discord.webhook.player_avatar_url") + player.getDisplayName(), finalLogLevel));
    }
}
