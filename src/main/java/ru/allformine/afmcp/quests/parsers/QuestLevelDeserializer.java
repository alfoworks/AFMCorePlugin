package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.quests.Quest;
import ru.allformine.afmcp.quests.QuestLevel;

import java.lang.reflect.Type;

public class QuestLevelDeserializer implements JsonDeserializer<QuestLevel> {
    @Override
    public QuestLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        QuestLevel result;

        Quest[] quests;

        try {
            JsonArray array = jsonObject.get("quests").getAsJsonArray();
            quests = new Quest[array.size()];
            for (int i = 0; i < array.size(); i++) {
                quests[i] = context.deserialize(array.get(i), Quest.class);
            }
        } catch (NullPointerException ignore) {
            quests = new Quest[0];
        }

        result = new QuestLevel(
                quests,
                TextSerializers.JSON.deserialize(jsonObject.get("levelId").getAsString()),
                jsonObject.get("itemTypeId").getAsString());


        return result;
    }
}
