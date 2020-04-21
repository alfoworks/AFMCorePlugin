package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;

public class FactionSerializer implements JsonSerializer<PlayerContribution> {
    @Override
    public JsonElement serialize(PlayerContribution src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        int complete = 0;
        try {
            for (Quest q : src.getActiveQuests()) {
                if (q.getTarget().getProgress() == q.getTarget().getCount()) {
                    complete++;
                }
            }
        } catch (NullPointerException e) {
          complete = 0;
        }

        result.addProperty("uuid", src.getPlayer().toString());
        result.addProperty("present", src.isPresent());
        result.addProperty("completed", complete);
        result.addProperty("factionTag", String.valueOf(src.getFaction().getTag()));

        return result;
    }
}
