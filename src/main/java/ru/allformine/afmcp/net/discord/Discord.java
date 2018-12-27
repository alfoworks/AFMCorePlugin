package ru.allformine.afmcp.net.discord;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.Requests;

public class Discord {
    private static void send(String message, boolean avatarFromName, String name, int logLevel, JavaPlugin plugin) {
        String url = plugin.getConfig().getString("discord.webhooks.url_lvl"+String.valueOf(logLevel));

        Map<String, String> JSON = new HashMap<>();
        JSON.put("content", message);

        if(avatarFromName) {
            JSON.put("avatar_url", plugin.getConfig().getString("discord.webhooks.player_avatar_url")+name);
        }
        if(name != null) {
            JSON.put("username", name);
        }

        String JSONString = new Gson().toJson(JSON);

        Requests.sendPost(JSONString, url);
    }

    public static void sendMessage(String message, boolean avatarFromName, String name, int logLevel, JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                send(message, avatarFromName, name, logLevel, plugin);
            }
        });
    }

    public static void sendMessageSync(String message, boolean avatarFromName, String name, int logLevel, JavaPlugin plugin) {
        send(message, avatarFromName, name, logLevel, plugin);
    }
}
