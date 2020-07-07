package ru.allformine.afmcp.quests;

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

    public String getLevelId() {
        return levelId;
    }

    public String getItemTypeId() {
        return itemTypeId;
    }
}
