package ru.allformine.afmcp.net.discord;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.References;
import ru.allformine.afmcp.net.Requests;

import java.util.HashMap;
import java.util.Map;

public class Discord {
    private static void send(String message, boolean avatarFromName, String name, int logLevel) {
        if (References.log) { //Лучше было бы, конечно, отключить слушатели ивентов, но чот лень...
            Plugin plugin = AFMCorePlugin.getPlugin();

            String url = plugin.getConfig().getString("discord.webhooks.url_lvl" + String.valueOf(logLevel));

            Map<String, String> JSON = new HashMap<>();
            JSON.put("content", message);

            if (avatarFromName) {
                JSON.put("avatar_url", plugin.getConfig().getString("discord.webhooks.player_avatar_url") + name);
            }
            if (name != null) {
                JSON.put("username", name);
            }

            String JSONString = new Gson().toJson(JSON);

            Requests.sendPost(JSONString, url);
        }
    }

    public static void sendMessage(String message, boolean avatarFromName, String name, int logLevel) {
        Bukkit.getScheduler().runTaskAsynchronously(AFMCorePlugin.getPlugin(), () -> send(message, avatarFromName, name, logLevel));
    }

    public static void sendMessageSync(String message, boolean avatarFromName, String name, int logLevel) {
        send(message, avatarFromName, name, logLevel);
    }
}
