package ru.allformine.afmcp.net.discord;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.JSONRequest;

public class Discord {
    private static void send(String message, boolean avatarFromName, String name, int logLevel) { //ебаный костыль
        String url;
        if(logLevel == 1) {
            url = "https://discordapp.com/api/webhooks/523289724639510548/cEkuF1hXAqH5KjCIHIpNUS2oNc-wvDgBjA7S2TETZt9Xu9uF5U8wKuHInx2vHYQmIOTJ";
        } else if(logLevel == 2) {
            url = "https://discordapp.com/api/webhooks/523488651330453564/Yes5EtngU8HmBnUoQKS3DZVWdghfd58cf3LD8w_WeWLxuQo3U91PZaKtEbEfNO-YVRiF";
        } else {
            throw new IllegalArgumentException("Unknown log level");
        }

        Map<String, String> JSON = new HashMap<>();
        JSON.put("content", message);

        if(avatarFromName) {
            JSON.put("avatar_url", "https://allformine.ru/banava?name="+name);
        }
        if(name != null) {
            JSON.put("username", name);
        }

        String JSONString = new Gson().toJson(JSON);

        JSONRequest.sendPost(JSONString, url);
    }

    public static void sendMessage(String message, boolean avatarFromName, String name, int logLevel, JavaPlugin plugin) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                send(message, avatarFromName, name, logLevel);
            }
        });
    }

    public static void sendMessageSync(String message, boolean avatarFromName, String name, int logLevel) {
        send(message, avatarFromName, name, logLevel);
    }
}
