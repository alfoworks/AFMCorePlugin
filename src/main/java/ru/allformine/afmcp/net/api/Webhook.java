package ru.allformine.afmcp.net.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import ninja.leaping.configurate.ConfigurationNode;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.http.Requests;

public class Webhook {
    private static ConfigurationNode configNode = AFMCorePlugin.Companion.getConfig().getNode("webhook");
    private static String server_id = configNode.getNode("server_id").getString();
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

    private static void sendApiRequest(JsonObject object, String type, String group, String[] extra) {
        object.addProperty("token", token);
        object.addProperty("server_id", server_id);
        object.addProperty("type", type);
        object.addProperty("group", group);
        object.add("arguments", arrayToJson(extra));
        final String json = object.toString();
        Requests.sendPostJSON(json, apiUrl);
    }

    private static JsonArray arrayToJson(String[] array) { // TODO: Дикий костыль
        JsonArray jsonArray = new JsonArray();
        for (String s : array) {
            jsonArray.add(s);
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

    public static void sendPlayerMessage(TypePlayerMessage type, CommandSource player, String... extra) {
        String typeName = type.name();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("username", player.getName());
        sendApiRequest(jsonObject, typeName, "player", extra);
    }
}
