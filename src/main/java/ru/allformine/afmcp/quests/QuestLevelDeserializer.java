package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestLevelDeserializer implements JsonDeserializer<QuestLevel> {
    @Override
    public QuestLevel deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        QuestLevel result;

        JsonArray array = jsonObject.get("quests").getAsJsonArray();
        Quest[] quests = new Quest[array.size()];
        for (int i = 0; i < array.size(); i++) {
            quests[i] = context.deserialize(array.get(i), Quest.class);
        }

        result = new QuestLevel(
                quests,
                jsonObject.get("levelId").getAsString(),
                jsonObject.get("itemTypeId").getAsString());


        return result;
    }
}
