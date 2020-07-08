package ru.allformine.afmcp.quests;

import java.util.Arrays;

public class QuestLevelContainer {
    private QuestLevel[] questLevels;

    public QuestLevelContainer(QuestLevel[] questLevels) {
        this.questLevels = questLevels;
    }

    public void setQuestLevels(QuestLevel[] questLevels) {
        this.questLevels = questLevels;
    }

    public void addQuestLevel(QuestLevel questLevel) {
        questLevels = Arrays.copyOf(questLevels, questLevels.length+1);
        questLevels[questLevels.length-1] = questLevel;
    }

    public QuestLevel[] getQuestLevels() {
        return questLevels;
    }

}
