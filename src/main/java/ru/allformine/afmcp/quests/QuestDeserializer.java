package ru.allformine.afmcp.quests;

import com.google.gson.*;

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

        JsonElement parent = jsonObject.get("parent");
        UUID realParent = null;
        if (!parent.isJsonNull()) {
            realParent = UUID.fromString(parent.getAsString());
        }

        Quest quest = new Quest(
            jsonObject.get("name").getAsString(),
            jsonObject.get("type").getAsString(),
            jsonObject.get("target").getAsString(),
            jsonObject.get("startMessage").getAsString(),
            jsonObject.get("finalMessage").getAsString(),
            jsonObject.get("lore").getAsString(),
            questEnd,
            Integer.parseInt(jsonObject.get("count").getAsString()),
            realParent
        );
        
        quest.setProgress(jsonObject.get("progress").getAsInt());
        return quest;
    }
}
