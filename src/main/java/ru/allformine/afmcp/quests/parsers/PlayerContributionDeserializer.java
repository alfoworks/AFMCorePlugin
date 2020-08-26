package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.lang.reflect.Type;
import java.util.Optional;
import java.util.UUID;


public class PlayerContributionDeserializer implements JsonDeserializer<PlayerContribution> {
    @Override
    public PlayerContribution deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        String player = object.get("player").getAsString();
        Optional<Faction> faction = EagleFactionsPlugin.getPlugin().getFactionLogic()
                .getFactionByPlayerUUID(UUID.fromString(player));

        assert faction.isPresent();

        PlayerContribution contribution = new PlayerContribution(
                player,
                faction.get().getName()
        );

        contribution.setQuestLevel(object.get("levelId").getAsString());

        for (JsonElement element: object.getAsJsonArray("activeQuests")) {
            contribution.assignQuest(context.deserialize(element, Quest.class));
        }

        for (JsonElement element: object.getAsJsonArray("completedQuests")) {
            contribution.assignQuest(context.deserialize(element, Quest.class));
        }

        return contribution;
    }
}
