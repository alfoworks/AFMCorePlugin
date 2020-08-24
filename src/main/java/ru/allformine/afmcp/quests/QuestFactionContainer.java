package ru.allformine.afmcp.quests;

import java.util.*;

public class QuestFactionContainer {
    private QuestFaction[] questFactions;

    public QuestFactionContainer() {
        questFactions = new QuestFaction[0];
    }

    public Optional<QuestFaction[]> getQuestFaction(UUID player) {
        QuestFaction[] past = {};

        for (QuestFaction faction: questFactions) {
            if (faction.hasInvestor(player)) {
                PlayerContribution contribution = faction.getContribution(player);
                if (!contribution.isPresent()) {
                    past = Arrays.copyOf(past, past.length + 1);
                    past[past.length - 1] = faction;
                }
            }
        }

        return Optional.of(past);
    }

    public Optional<QuestFaction> getActiveQuestFaction(UUID player) {
        if (questFactions == null) {
            return Optional.empty();
        }

        for (QuestFaction faction: questFactions) {
            if (faction.hasInvestor(player)) {
                PlayerContribution contribution = faction.getContribution(player);
                if (contribution.isPresent()) {
                    return Optional.of(faction);
                }
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
            if (questFactions[i].getTag().equals(faction.getTag())) {
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

    private void trimQuestFactions() {
        List<QuestFaction> okFactions = new ArrayList<>();

        for (QuestFaction faction: questFactions) {
            if (faction != null)
                okFactions.add(faction);
        }

        questFactions = new QuestFaction[okFactions.size()];
        okFactions.toArray(questFactions);
    }

    public void disbandQuestFaction(QuestFaction faction) {
        for (int i = 0; i < questFactions.length; i++) {
            if (questFactions[i].getTag().equals(faction.getTag())) {
                questFactions[i] = null;
                trimQuestFactions();
            }
        }
    }
}
