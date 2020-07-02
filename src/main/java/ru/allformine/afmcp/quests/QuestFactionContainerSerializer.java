package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class QuestFactionContainerSerializer implements JsonSerializer<QuestFactionContainer> {
    @Override
    public JsonElement serialize(QuestFactionContainer src, Type typeOfSrc, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (QuestFaction faction: src.getQuestFactions()) {
            array.add(context.serialize(faction, QuestFaction.class));
        }

        return array;
    }
}
