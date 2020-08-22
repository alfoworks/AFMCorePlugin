package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.quests.Quest;
import ru.allformine.afmcp.quests.QuestLevel;

import java.lang.reflect.Type;

public class QuestLevelSerializer implements JsonSerializer<QuestLevel> {
    @Override
    public JsonElement serialize(QuestLevel src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        JsonArray array = new JsonArray();

        if (src.getQuests() != null) {
            for (Quest q : src.getQuests()) {
                array.add(context.serialize(q));
            }
        }

        result.addProperty("levelId", TextSerializers.JSON.serialize(src.getLevelId()));
        result.addProperty("itemTypeId", src.getItemTypeId());
        result.add("quests", array);

        return result;
    }
}
