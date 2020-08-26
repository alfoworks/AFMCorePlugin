package ru.allformine.afmcp.quests;

import java.util.*;

public class QuestFactionContainer {
    private QuestFaction[] questFactions;

    public QuestFactionContainer() {
        questFactions = new QuestFaction[0];
    }

    public Optional<QuestFaction> getQuestFaction(UUID player) {
        if (questFactions == null) {
            return Optional.empty();
        }

        for (QuestFaction faction: questFactions) {
            if (faction.hasInvestor(player)) {
                return Optional.of(faction);
            }
        }

        return Optional.empty();
    }

    public Optional<QuestFaction> getQuestFaction(String factionName) {
        if (questFactions == null) {
            return Optional.empty();
        }

        for (QuestFaction faction: questFactions) {
            if (faction.getName().toLowerCase().equals(factionName.toLowerCase())) {
                return Optional.of(faction);
            }
        }
        return Optional.empty();
    }

    public QuestFaction[] getQuestFactions() {
        return questFactions;
    }

    public void updateQuestFaction(QuestFaction faction) {
        for (int i = 0; i < questFactions.length; i++) {
            if (questFactions[i].getName().equals(faction.getName())) {
                if (!faction.getContribution(faction.getCurrentLeader()).getFactionName().equals(faction.getName()))
                    faction.setName(faction.getContribution(faction.getCurrentLeader()).getFactionName());
                questFactions[i] = faction;
                break;
            }
        }
    }

    public void createQuestFaction(QuestFaction faction) {
        if (questFactions != null) {
            questFactions = Arrays.copyOf(questFactions, questFactions.length+1);
            questFactions[questFactions.length-1] = faction;
        } else {
            questFactions = new QuestFaction[1];
            questFactions[0] = faction;
        }
    }

    public void disbandQuestFaction(QuestFaction faction) {
        for (int i = 0; i < questFactions.length; i++) {
            if (questFactions[i].getName().equalsIgnoreCase(faction.getName())) {
                questFactions[i] = questFactions[questFactions.length - 1];
                questFactions = Arrays.copyOf(questFactions, questFactions.length - 1);
            }
        }
    }
}
