package ru.allformine.afmcp.quests;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spongepowered.api.text.serializer.TextSerializers;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class QuestSerializer implements JsonSerializer<Quest> {
    @Override
    public JsonElement serialize(Quest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");


        result.addProperty("name", TextSerializers.JSON.serialize(src.getName()));
        result.addProperty("type", src.getType());
        result.addProperty("target", src.getTarget());
        result.addProperty("progress", src.getProgress());
        result.addProperty("count", src.getCount());
        result.addProperty("startMessage", TextSerializers.JSON.serialize(src.getStartMessage()));
        result.addProperty("finalMessage", TextSerializers.JSON.serialize(src.getFinalMessage()));
        result.addProperty("lore", TextSerializers.JSON.serialize(src.getLore()));
        if (src.getQuestEnd() != null) {
            result.addProperty("timeLimit", simpleDateFormat.format(src.getQuestEnd()));
        } else {
            result.add("timeLimit", null);
        }

        if (src.getParent() != null) {
            result.addProperty("parent", src.getParent().toString());
        } else {
            result.add("parent", null);
        }

        return result;
    }
}
