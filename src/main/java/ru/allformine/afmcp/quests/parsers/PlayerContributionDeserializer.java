package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import ibxm.Player;
import net.minecraftforge.event.world.NoteBlockEvent;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.lang.reflect.Type;
import java.util.*;


public class PlayerContributionDeserializer implements JsonDeserializer<PlayerContribution> {
    @Override
    public PlayerContribution deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject object = json.getAsJsonObject();
        PlayerContribution contribution = new PlayerContribution(
                object.get("player").getAsString(),
                object.get("factionName").getAsString()
        );

        contribution.setQuestLevel(object.get("levelId").getAsString());
        contribution.setPresent(object.get("present").getAsBoolean());

        for (JsonElement element: object.getAsJsonArray("activeQuests")) {
            contribution.assignQuest(context.deserialize(element, Quest.class));
        }

        for (JsonElement element: object.getAsJsonArray("completedQuests")) {
            contribution.assignQuest(context.deserialize(element, Quest.class));
        }

        return contribution;
    }
}
