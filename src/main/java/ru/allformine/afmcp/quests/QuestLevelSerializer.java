package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestLevelSerializer implements JsonSerializer<QuestLevel> {
    @Override
    public JsonElement serialize(QuestLevel src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        JsonArray array = new JsonArray();
        for (Quest q : src.getQuests()) {
            array.add(context.serialize(q));
        }

        result.addProperty("levelId", src.getLevelId());
        result.addProperty("itemTypeId", src.getItemTypeId());
        result.add("quests", array);

        return result;
    }
}
