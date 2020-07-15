package ru.allformine.afmcp.quests;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKey;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.profile.property.ProfileProperty;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

// DataClass which represents Player Contribution to a certain faction
public class PlayerContribution {
    private final String factionName;
    private Quest[] completedQuests;
    private Quest[] activeQuests;
    private boolean present;
    private final UUID player;
    private QuestLevel questLevel;

    public PlayerContribution(Player player, Faction faction) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = player.getUniqueId();
        this.factionName = faction.getName();
        this.present = true;
        this.activeQuests = new Quest[config.getNode("quests", "activeLimit").getInt()];
        this.completedQuests = new Quest[0];
    }

    public PlayerContribution(String player, String factionName) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = UUID.fromString(player);
        this.factionName = factionName;
        this.activeQuests = new Quest[config.getNode("quests", "activeLimit").getInt()];
    }

    public boolean containsName(final String name){
        return completedQuests != null && Arrays.stream(completedQuests).anyMatch(o -> o.getName().equals(name));
    }

    public boolean assignQuest(Quest quest) {
        quest.setParent(player);
        if (containsName(quest.getName().toPlain()))
            return false;
        for (int i = 0; i < activeQuests.length; i++) {
            if (activeQuests[i] == null) {
                activeQuests[i] = quest;
                if (quest.finished())
                    completeQuest(quest);
                return true;
            }
        }
        return false;
    }

    public void updateQuest(Quest quest) {
        for (int i = 0; i < activeQuests.length; i++) {
            if (activeQuests[i] != null) {
                if (activeQuests[i].getName().equals(quest.getName())) {
                    activeQuests[i] = quest;
                    if (quest.finished())
                        completeQuest(quest);
                    break;
                }
            }
        }
    }

    public void completeQuest(Quest quest) {
        Quest temp = null;
        if (quest.finished()) {
            for (int i = 0; i < activeQuests.length; i++) {
                if (activeQuests[i] == null) continue;
                if (activeQuests[i].getName().equals(quest.getName())) {
                    temp = activeQuests[i];
                    activeQuests[i] = null;
                    break;
                }
            }
        }
        if (temp != null && completedQuests == null) {
            Quest[] tempX = new Quest[1];
            tempX[0] = temp;
            resetCompletedQuests(tempX);
        } else if (temp != null) {
            Quest[] tempX = Arrays.copyOf(completedQuests, completedQuests.length+1);
            tempX[tempX.length-1] = temp;
            resetCompletedQuests(tempX);
        }
    }

    public Quest getQuest(String name) {
        for (Quest q: activeQuests)
            if (q.getName().equals(name))
                return q;

        return null;
    }

    public void resetCompletedQuests(Quest[] quests) {
        this.completedQuests = quests;
    }

    public Faction getFaction() {
        return EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(factionName);
    }

    public String getFactionName() {
        return factionName;
    }

    public Quest[] getCompletedQuests() {
        return completedQuests;
    }

    public Quest[] getActiveQuests() {
        return activeQuests;
    }

    public UUID getPlayer() {
        return player;
    }

    public boolean isPresent() {
        return present;
    }

    public String toString() {
        return String.format("%s | %s", getPlayer(), getFaction());
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public QuestLevel getLevel() {
        return questLevel;
    }

    public void setQuestLevel(QuestLevel questLevel) {
        this.questLevel = questLevel;
    }
}
