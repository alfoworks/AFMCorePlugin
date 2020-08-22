package ru.allformine.afmcp.quests.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ru.allformine.afmcp.quests.QuestLevel;
import ru.allformine.afmcp.quests.QuestLevelContainer;

import java.lang.reflect.Type;

public class QuestLevelContainerSerializer implements JsonSerializer<QuestLevelContainer> {
    @Override
    public JsonElement serialize(QuestLevelContainer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();

        for (QuestLevel l : src.getQuestLevels()) {
            array.add(context.serialize(l));
        }

        return array;
    }
}
