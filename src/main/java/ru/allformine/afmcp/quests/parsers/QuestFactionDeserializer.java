package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.QuestFaction;

import java.lang.reflect.Type;
import java.util.UUID;

public class QuestFactionDeserializer implements JsonDeserializer<QuestFaction> {
    @Override
    public QuestFaction deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        QuestFaction questFaction = new QuestFaction(
                                                    jsonObject.get("name").getAsString(),
                                                    TextSerializers.JSON.deserialize(jsonObject.get("tag").getAsString()));

        questFaction.setFactionPower(jsonObject.get("factionPower").getAsInt());
        questFaction.setCurrentLeader(UUID.fromString(jsonObject.get("currentLeader").getAsString()));

        JsonArray array = jsonObject.getAsJsonArray("investors");
        PlayerContribution[] contributions = new PlayerContribution[array.size()];
        for (int i = 0; i < array.size(); i++) {
            contributions[i] = context.deserialize(array.get(i), PlayerContribution.class);
        }

        questFaction.setInvestors(contributions);

        return questFaction;
    }
}
