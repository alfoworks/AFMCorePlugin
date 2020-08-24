package ru.allformine.afmcp.quests;

public class QuestLevelContainer {
    private final QuestLevel[] questLevels;

    public QuestLevelContainer(QuestLevel[] questLevels) {
        this.questLevels = questLevels;
    }

    public QuestLevel[] getQuestLevels() {
        return questLevels;
    }

    public QuestLevel getLevelById(String levelId) {
        for (QuestLevel q: questLevels) {
            if (q.getLevelId().toPlain().equals(levelId))
                return q;
        }
        return null;
    }
}
