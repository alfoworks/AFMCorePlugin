package ru.allformine.afmcp.net.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.Requests;

public class Webhook {
    private static ConfigurationNode configNode = AFMCorePlugin.getConfig().getNode("discord");
    private static String server_id = configNode.getNode("server_id").getString();
    private static Gson builder = new GsonBuilder().create();
    private static String token = configNode.getNode("token").getString();
    private static String apiUrl = configNode.getNode("apiURL").getString();

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

    private static void sendApiRequest(JsonObject object) {
        Requests.sendPostJSON(object.toString(), apiUrl);
    }

    public static void sendSecureAlert(TypeSecureAlert type, String text, Player player, String... extra) {
        String typeName = type.name();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server_id", server_id);
        jsonObject.addProperty("group", "secalert");
        jsonObject.addProperty("type", typeName);
        jsonObject.addProperty("arguments", builder.toJson(extra));
        jsonObject.addProperty("token", token);
        sendApiRequest(jsonObject);
    }

    public static void sendServerMessage(TypeServerMessage type, String... extra) {
        String typeName = type.name();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server_id", server_id);
        jsonObject.addProperty("group", "server");
        jsonObject.addProperty("type", typeName);
        jsonObject.addProperty("arguments", builder.toJson(extra));
        jsonObject.addProperty("token", token);
        sendApiRequest(jsonObject);
    }

    public static void sendPlayerMessage(TypePlayerMessage type, Player player, String... extra) {
        String typeName = type.name();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("server_id", server_id);
        jsonObject.addProperty("group", "player");
        jsonObject.addProperty("username", player.getName());
        jsonObject.addProperty("type", typeName);
        jsonObject.addProperty("arguments", builder.toJson(extra));
        jsonObject.addProperty("token", token);
        sendApiRequest(jsonObject);
    }
}
