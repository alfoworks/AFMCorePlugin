package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestFactionContainerDeserializer implements JsonDeserializer<QuestFactionContainer> {


    @Override
    public QuestFactionContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        QuestFactionContainer container = new QuestFactionContainer();
        JsonArray array = json.getAsJsonArray();

        for (JsonElement element: array){
            container.createQuestFaction(context.deserialize(element, QuestFaction.class));
        }

        return container;
    }
}
