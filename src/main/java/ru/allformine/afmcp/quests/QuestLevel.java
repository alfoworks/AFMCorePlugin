package ru.allformine.afmcp.quests;

import org.spongepowered.api.text.Text;

import java.util.Arrays;
import java.util.Optional;

public class QuestLevel {
    private final Quest[] quests;
    private final Text levelId;
    private final String itemTypeId;

    public QuestLevel(Quest[] quests, Text levelId, String itemTypeId) {
        this.quests = quests;
        this.levelId = levelId;
        this.itemTypeId = itemTypeId;
    }

    public Quest[] getQuests() {
        return quests;
    }

    public Quest getQuest(int id) {
        return quests[id];
    }

    public Quest getQuest(String name) {
        if (quests != null) {
            Optional<Quest> quest = Arrays.stream(quests).filter(q -> q.getName().toPlain().equals(name)).findFirst();
            return quest.orElse(null);
        }
        return null;
    }

    public Text getLevelId() {
        return levelId;
    }

    public String getItemTypeId() {
        return itemTypeId;
    }
}
