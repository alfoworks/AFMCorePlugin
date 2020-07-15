package ru.allformine.afmcp.quests;

import java.util.Arrays;
import java.util.Optional;

public class QuestLevel {
    private final Quest[] quests;
    private final String levelId;
    private final String itemTypeId;

    public QuestLevel(Quest[] quests, String levelId, String itemTypeId) {
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

    public String getLevelId() {
        return levelId;
    }

    public String getItemTypeId() {
        return itemTypeId;
    }
}
