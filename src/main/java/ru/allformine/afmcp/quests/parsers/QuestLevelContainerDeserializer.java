package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import ru.allformine.afmcp.quests.QuestLevel;
import ru.allformine.afmcp.quests.QuestLevelContainer;

import java.lang.reflect.Type;

public class QuestLevelContainerDeserializer implements JsonDeserializer<QuestLevelContainer> {

    @Override
    public QuestLevelContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        QuestLevel[] levels = new QuestLevel[array.size()];

        for (int i = 0; i < levels.length; i++) {
            levels[i] = context.deserialize(array.get(i), QuestLevel.class);
        }

        return new QuestLevelContainer(levels);
    }
}
