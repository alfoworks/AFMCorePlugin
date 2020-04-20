package ru.allformine.afmcp.quests;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.ArrayList;
import java.util.UUID;

// DataClass which represents Player Contribution to a certain faction
//// TODO: AutoSave data on any set call to this DataClass
public class PlayerContribution {
    private final String factionName;
    private ArrayList<Quest> completedQuests;
    private Quest[] activeQuests;
    private boolean present;
    private final UUID player;

    public PlayerContribution(Player player, Faction faction) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = player.getUniqueId();
        this.factionName = faction.getName();
        this.activeQuests = new Quest[config.getNode("quests", "activeLimit").getInt()];
    }

    public boolean assignQuest(Quest quest) {
        for (int i = 0; i < activeQuests.length; i++) {
            if (activeQuests[i] == null) {
                activeQuests[i] = quest;
                return true;
            }
        }
        return false;
    }

    public Faction getFaction() {
        return EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(factionName);
    }

    public String getFactionName() {
        return factionName;
    }

    public ArrayList<Quest> getCompletedQuests() {
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
        return String.format("%s - %s\n%s - %s/%s", getPlayer(), isPresent(),
                getFaction(), activeQuests.length, completedQuests.size());
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
