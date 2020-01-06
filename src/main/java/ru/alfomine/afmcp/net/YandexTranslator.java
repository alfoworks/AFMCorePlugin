package ru.alfomine.afmcp.net;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import ru.alfomine.afmcp.PluginStatics;

import java.util.ArrayList;

public class YandexTranslator {
    private static String translateString(String text, String lang) {
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

    public static String retranslate(String text) {
        if (!PluginStatics.debugRetranslateEnabled) {
            return text;
        }

        if (PluginStatics.debugTranslateCache.containsKey(text)) {
            return PluginStatics.debugTranslateCache.get(text);
        }

        String translatedChinese = YandexTranslator.translateString(text, "en");

        if (translatedChinese == null) {
            return null;
        }

        String retranslated = YandexTranslator.translateString(translatedChinese, "ru");

        if (retranslated == null) {
            return null;
        }

        PluginStatics.debugTranslateCache.put(text, retranslated);

        return retranslated;
    }
}
