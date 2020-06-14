package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class PlayerContribtuionSerializer implements JsonSerializer<PlayerContribution> {
    @Override
    public JsonElement serialize(PlayerContribution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        String factionName;

        try {
            factionName = src.getFaction().getName();
        } catch (NullPointerException ignore) {
            factionName = "";
        }

        JsonArray completed = new JsonArray();
        JsonArray active = new JsonArray();

        if (src.getCompletedQuests() != null) {
            for (Quest quest: src.getCompletedQuests())
                completed.add(context.serialize(quest));
        }

        for (Quest quest: src.getActiveQuests())
            if (quest != null)
                active.add(context.serialize(quest));

        result.addProperty("player", src.getPlayer().toString());
        result.addProperty("present", src.isPresent());
        result.addProperty("factionName", factionName);
        result.add("completedQuests", completed);
        result.add("activeQuests", active);

        return result;
    }
}
