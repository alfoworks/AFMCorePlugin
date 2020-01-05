package ru.alfomine.afmcp.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.alfomine.afmcp.PluginStatics;

import java.util.ArrayList;

public class YandexTranslator {
    public static String translateString(String text, String lang) {
        String jsonString = getTranslatedJson(text, lang);

        if (jsonString == null) {
            return null;
        }

        JsonObject json = new Gson().fromJson(jsonString, JsonObject.class);
        ArrayList<String> out = new ArrayList<>();

        for (JsonElement element : json.getAsJsonArray("text")) {
            out.add(element.getAsString());
        }

        return String.join(" ", out);
    }

    private static String getTranslatedJson(String text, String lang) {
        return Request.sendNewGet("https://translate.yandex.net/api/v1.5/tr.json/translate", new String[]{"key", PluginStatics.debugTranslatorKey}, new String[]{"text", text}, new String[]{"lang", lang});
    }
}
