package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FactionSerializer implements JsonSerializer<PlayerContribution> {
    @Override
    public JsonElement serialize(PlayerContribution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        String factionName;
        String factionTag;

        try {
            factionName = src.getFaction().getName();
            factionTag = src.getFaction().getTag().toString();
        } catch (NullPointerException ignore) {
            factionName = "";
            factionTag = "";
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
        result.addProperty("factionTag", factionTag);
        result.add("completedQuests", completed);
        result.add("activeQuests", active);

        return result;
    }
}
