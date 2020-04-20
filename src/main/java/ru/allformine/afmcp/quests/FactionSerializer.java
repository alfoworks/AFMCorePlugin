package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FactionSerializer implements JsonSerializer<PlayerContribution> {
    @Override
    public JsonElement serialize(PlayerContribution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        int active = 0;
        int complete = 0;
        try {
            for (Quest q : src.getActiveQuests()) {
                if (q.getTarget().getProgress() < q.getTarget().getCount()) {
                    active++;
                } else {
                    complete++;
                }
            }
        } catch (NullPointerException e) {
          active = 0;
          complete = 0;
        }

        result.addProperty("uuid", src.getPlayer().toString());
        result.addProperty("present", src.isPresent());
        result.addProperty("activeQuests", active);
        result.addProperty("completedQuests", complete);
        result.addProperty("factionName", src.getFaction().getName());

        return result;
    }
}
