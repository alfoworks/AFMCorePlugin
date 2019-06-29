package ru.allformine.afmcp.net.api;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonArray;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonObject;
import org.bukkit.craftbukkit.libs.com.google.gson.JsonPrimitive;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.Requests;

public class Webhook {
    private static FileConfiguration configNode = AFMCorePlugin.getPlugin().getConfig();
    private static String server_id = configNode.getString("webhook.server_id");
    private static String token = configNode.getString("webhook.token");
    private static String apiUrl = configNode.getString("webhook.apiURL");


    public enum TypeServerMessage {
        SERVER_STARTED,
        SERVER_STOPPED,
        SERVER_RESTARTING,
        SERVER_WIPE,
        SERVER_MAINTENANCE
    }

    public enum TypePlayerMessage {
        CHAT_MESSAGE,
        LVL2_CHAT_MESSAGE,
        COMMAND,
        JOINED_SERVER,
        LEFT_SERVER,
        STAFF_JOINED_SERVER,
        STAFF_LEFT_SERVER,
        JOINED_FIRST_TIME,
        EARNED_ADVANCEMENT,
        DIED,
        BOUGHT_VIP,
    }

    public enum TypeSecureAlert {
        PACKETHACK_USAGE_DETECTED,
    }

    private static void sendApiRequest(JsonObject object, String type, String group, String[] extra) {
        object.addProperty("token", token);
        object.addProperty("server_id", server_id);
        object.addProperty("type", type);
        object.addProperty("group", group);
        object.add("arguments", arrayToJson(extra));
        final String json = object.toString();
        Requests.sendPost(json, apiUrl);
    }

    private static JsonArray arrayToJson(String[] array) { // TODO: Дикий костыль
        JsonArray jsonArray = new JsonArray();
        for (String s : array) {
            jsonArray.add(new JsonPrimitive(s));
        }
        return jsonArray;
    }

    public static void sendSecureAlert(TypeSecureAlert type, Player player, String... extra) {
        JsonObject object = new JsonObject();
        object.addProperty("username", player.getName());
        sendApiRequest(object, type.name(), "secalert", extra);
    }

    public static void sendServerMessage(TypeServerMessage type, String... extra) {
        sendApiRequest(new JsonObject(), type.name(), "server", extra);
    }

    public static void sendPlayerMessage(TypePlayerMessage type, Player player, String... extra) {
        String typeName = type.name();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", player.getName());
        sendApiRequest(jsonObject, typeName, "player", extra);
    }
}
