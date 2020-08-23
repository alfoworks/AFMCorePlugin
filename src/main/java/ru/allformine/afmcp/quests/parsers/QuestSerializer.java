package ru.allformine.afmcp.quests.parsers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.Quest;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

public class QuestSerializer implements JsonSerializer<Quest> {
    @Override
    public JsonElement serialize(Quest src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if (src.getParent() == null) {
            // Editor Serializer
            result.addProperty("name", TextSerializers.JSON.serialize(src.getName()));
            result.addProperty("type", src.getType());
            result.addProperty("target", src.getTarget());
            result.addProperty("count", src.getCount());
            result.addProperty("startMessage", TextSerializers.JSON.serialize(src.getStartMessage()));
            result.addProperty("finalMessage", TextSerializers.JSON.serialize(src.getFinalMessage()));
            result.addProperty("lore", TextSerializers.JSON.serialize(src.getLore()));

        } else {
            // Faction Serializer
            // It's way more abstract for the sake of economy
            String levelId = AFMCorePlugin.questDataManager.getContribution(src.getParent()).getLevelId();
            int questId = AFMCorePlugin.questDataManager.getQuestDifficulties().getLevelById(levelId).getQuestId(src.getName().toPlain());
            assert questId != -1; // Means that quest doesn't exist
            result.addProperty("levelId", levelId);
            result.addProperty("questId", questId);
            result.addProperty("parent", src.getParent().toString());
        }

        result.addProperty("progress", src.getProgress());

        //// TODO: Time Limited Quests
        if (src.getQuestEnd() != null) {
            result.addProperty("timeLimit", simpleDateFormat.format(src.getQuestEnd()));
        } else {
            result.add("timeLimit", null);
        }


        return result;
    }
}
