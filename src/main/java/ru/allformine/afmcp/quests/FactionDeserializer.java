package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class FactionDeserializer implements JsonDeserializer<Map<String, PlayerContribution[]>> {

    @Override
    public Map<String, PlayerContribution[]> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject factions = json.getAsJsonObject();
        Map<String, PlayerContribution[]> result = new HashMap<>();
        for (Map.Entry<String, JsonElement> e : factions.entrySet()) {
            JsonArray contributionsJson = e.getValue().getAsJsonArray();
            List<PlayerContribution> playerContributions = new ArrayList<>();

            for (JsonElement je : contributionsJson) {
                JsonObject playerContribution = je.getAsJsonObject();
                PlayerContribution realContribution =
                        new PlayerContribution(
                                playerContribution.get("player").getAsString(),
                                playerContribution.get("factionName").getAsString(),
                                playerContribution.get("factionTag").getAsString()
                        );

                realContribution.setPresent(playerContribution.get("present").getAsBoolean());
                playerContributions.add(realContribution);
            }

            // To remove any chance of negative arraysize
            if (playerContributions.size() == 0) playerContributions.add(null);

            PlayerContribution[] resultTemp = new PlayerContribution[playerContributions.size()];
            playerContributions.toArray(resultTemp);
            result.put(playerContributions.get(0).getFactionName(), resultTemp);
        }

        return result;
    }
}
