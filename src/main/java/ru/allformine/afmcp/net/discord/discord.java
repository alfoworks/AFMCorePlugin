package ru.allformine.afmcp.net.discord;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import ru.allformine.afmcp.net.request;

public class discord {
    public static void sendMessage(String message, boolean avatarFromName, String name) {
        Map<String, String> JSON = new HashMap<String, String>();
        JSON.put("content", message);

        if(avatarFromName) {
            JSON.put("avatar_url", "https://allformine.ru/banava?name="+name);
        }
        if(name != null) {
            JSON.put("username", name);
        }

        String JSONString = new Gson().toJson(JSON);

        System.out.println("JSONDebug: "+JSONString);
        request.sendPost(JSONString, "https://discordapp.com/api/webhooks/523289724639510548/cEkuF1hXAqH5KjCIHIpNUS2oNc-wvDgBjA7S2TETZt9Xu9uF5U8wKuHInx2vHYQmIOTJ");
    }
}
