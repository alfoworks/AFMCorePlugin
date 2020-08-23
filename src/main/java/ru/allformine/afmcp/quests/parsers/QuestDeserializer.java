package ru.allformine.afmcp.quests.parsers;

import com.google.gson.*;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.Quest;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class QuestDeserializer implements JsonDeserializer<Quest> {
    @Override
    public Quest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        /*
                 String name,
                 String type,
                 String target,
                 String startMessage,
                 String finalMessage,
                 String lore,
                 Calendar questStart,
                 int timeLimit, // In minutes
                 int count,
                 PlayerContribution parent
         */

        JsonElement unknownLimit = jsonObject.get("timeLimit");
        Date questEnd;

        if (unknownLimit.isJsonNull()) {
            questEnd = null;
        } else {
            try {
                String dateTime = unknownLimit.getAsString();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                questEnd = simpleDateFormat.parse(dateTime);
            } catch (Exception e) {
                int timeLimit = unknownLimit.getAsInt();
                if (timeLimit != 0) {
                    Calendar calender = Calendar.getInstance();
                    calender.add(Calendar.MINUTE, timeLimit);
                    questEnd = calender.getTime();
                } else {
                    questEnd = null; // No time limit
                }
            }
        }

        Quest quest;
        JsonElement parent = jsonObject.get("parent");
        UUID realParent;
        if (!parent.isJsonNull()) {
            realParent = UUID.fromString(parent.getAsString());
            String levelId = jsonObject.get("levelId").getAsString();
            quest = AFMCorePlugin.questDataManager
                    .getQuestById(levelId, jsonObject.get("questId").getAsInt());
            quest.setParent(realParent);
        } else {
            quest = new Quest(
                    TextSerializers.JSON.deserialize(jsonObject.get("name").getAsString()),
                    jsonObject.get("type").getAsString(),
                    jsonObject.get("target").getAsString(),
                    TextSerializers.JSON.deserialize(jsonObject.get("startMessage").getAsString()),
                    TextSerializers.JSON.deserialize(jsonObject.get("finalMessage").getAsString()),
                    TextSerializers.JSON.deserialize(jsonObject.get("lore").getAsString()),
                    questEnd,
                    Integer.parseInt(jsonObject.get("count").getAsString()),
                    null
            );
        }

        
        quest.setProgress(jsonObject.get("progress").getAsInt());
        return quest;
    }
}
