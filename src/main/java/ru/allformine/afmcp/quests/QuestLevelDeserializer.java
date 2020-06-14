package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.Map;

// Quest levels are readonly. So only deserializer is present.
public class QuestLevelDeserializer implements JsonDeserializer<QuestLevel[]> {
    @Override
    public QuestLevel[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        QuestLevel[] result = new QuestLevel[jsonObject.entrySet().size()];

        // A lot of loops there. No need for optimization because this is called rarely.
        for (Map.Entry<String, JsonElement> entry: jsonObject.entrySet()) {
            JsonArray array = entry.getValue().getAsJsonArray();
            Quest[] quests = new Quest[array.size()];
            for (int i = 0; i < array.size(); i++) {
                quests[i] = context.deserialize(array.get(i), Quest.class);
            }

            for (int i = 0; i < result.length; i++) {
                if (result[i] == null) {
                    QuestLevel level = new QuestLevel();
                    level.setQuests(quests);
                    result[i] = level;
                    break;
                }
            }
        }

        return result;
    }
}
