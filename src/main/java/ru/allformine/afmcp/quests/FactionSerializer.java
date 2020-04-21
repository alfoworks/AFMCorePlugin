package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FactionSerializer implements JsonSerializer<PlayerContribution> {
    @Override
    public JsonElement serialize(PlayerContribution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        String[] activeQuests = new String[src.getActiveQuests().length];

        for (int i = 0; i < src.getActiveQuests().length; i++) {
            Quest q = src.getActiveQuests()[i];
            if (q == null) {
                activeQuests[i] = "";
                continue;
            }

            if (q.getTarget().getProgress() == q.getTarget().getCount())
                src.completeQuest(q);
            else
                activeQuests[i] = q.toString();
        }

        Gson gson = new Gson();
        String factionName;
        String factionTag;

        try {
            factionName = src.getFaction().getName();
            factionTag = src.getFaction().getTag().toString();
        } catch (NullPointerException e) {
            factionName = "";
            factionTag = "";
        }

        result.addProperty("player", src.getPlayer().toString());
        result.addProperty("present", src.isPresent());
        result.addProperty("completed", src.getCompletedQuests());
        result.addProperty("factionName", factionName);
        result.addProperty("factionTag", factionTag);
        result.add("activeQuests", gson.toJsonTree(activeQuests));

        return result;
    }
}
