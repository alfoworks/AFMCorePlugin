package ru.allformine.afmcp.quests;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.*;

public class FactionDeserializer implements JsonDeserializer<Map<String, PlayerContribution[]>> {

    private Quest deserializeQuest(JsonElement x) {
        String[] c = x.getAsString().split("/");
        Quest q = new Quest(c[5], c[0], c[1], Integer.parseInt(c[2]), Integer.parseInt(c[3]));
        q.getTarget().setProgress(Integer.parseInt(c[1]));
        return q;
    }

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
                Quest[] queue = new Quest[playerContribution.get("completedQuests").getAsJsonArray().size()];

                playerContribution.get("completedQuests").getAsJsonArray()
                        .iterator()
                        .forEachRemaining(x -> {
                            for (int i = 0; i < queue.length; i++) {
                                if (queue[i] == null && x != null) {
                                    queue[i] = deserializeQuest(x);
                                }
                            }
                        });
                playerContribution.get("activeQuests").getAsJsonArray()
                        .iterator()
                        .forEachRemaining(x -> {
                            if (!x.getAsString().equals("")) {
                                realContribution.assignQuest(deserializeQuest(x));
                            }
                        });

                realContribution.resetCompletedQuests(queue);
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
