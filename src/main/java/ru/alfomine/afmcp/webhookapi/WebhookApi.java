package ru.alfomine.afmcp.webhookapi;

import com.google.gson.Gson;
import org.bukkit.Bukkit;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.HashMap;

public class WebhookApi {
    public static void sendPlayerMessage(MessageTypePlayer messageType, String username, String... args) {
        HashMap<String, Object> map = new HashMap<>();

        map.put("type", messageType.name());
        map.put("group", "player");
        map.put("server_id", PluginConfig.serverId);
        map.put("username", username);
        map.put("arguments", args);

        Bukkit.getServer().getScheduler().runTaskAsynchronously(AFMCorePlugin.getPlugin(), () -> Request.sendNewPost(new Gson().toJson(map), PluginConfig.webhookApiUrl));
    }

    public static void sendServerMessage(MessageTypeServer messageType) {
        HashMap<String, String> map = new HashMap<>();

        map.put("type", messageType.name());
        map.put("group", "server");
        map.put("server_id", PluginConfig.serverId);

        Request.sendNewPost(new Gson().toJson(map), PluginConfig.webhookApiUrl);
    }

    // ===================================== //
}
