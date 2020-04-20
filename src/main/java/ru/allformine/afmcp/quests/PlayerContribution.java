package ru.allformine.afmcp.quests;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import org.spongepowered.api.entity.living.player.Player;
import ru.allformine.afmcp.AFMCorePlugin;

import java.util.ArrayList;

// DataClass which represents Player Contribution to a certain faction
//// TODO: AutoSave data on any set call to this DataClass
public class PlayerContribution {
    private Faction faction;
    private ArrayList<Quest> completedQuests;
    private Quest[] activeQuests;
    private boolean present;
    private Player player;

    public PlayerContribution(Player player, Faction faction) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = player; //// TODO: Synchronization tests
        this.faction = faction;
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
        return faction;
    }

    public ArrayList<Quest> getCompletedQuests() {
        return completedQuests;
    }

    public Quest[] getActiveQuests() {
        return activeQuests;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
