package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.QuestFaction;

import java.lang.reflect.Type;

public class QuestFactionSerializer implements JsonSerializer<QuestFaction> {
    @Override
    public JsonElement serialize(QuestFaction src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();

        JsonArray investors = new JsonArray();
        for (PlayerContribution contribution: src.getInvestors()) {
            investors.add(context.serialize(contribution));
        }

        result.addProperty("name", src.getName());
        result.addProperty("factionPower", src.getFactionPower());
        result.addProperty("currentLeader", src.getCurrentLeader().toString());
        result.add("investors", investors);

        return result;
    }
}
