package ru.allformine.afmcp.quests;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class QuestLevelContainerDeserializer implements JsonDeserializer<QuestLevelContainer> {
    @Override
    public QuestLevelContainer deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return null;
    }
}
