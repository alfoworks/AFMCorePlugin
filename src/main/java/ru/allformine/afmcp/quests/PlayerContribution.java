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
import ru.allformine.afmcp.quests.events.QuestAssignedEvent;
import ru.allformine.afmcp.quests.events.QuestAssignedEventImpl;

import java.util.UUID;

// DataClass which represents Player Contribution to a certain faction
public class PlayerContribution {
    private final String factionName;
    private final Text factionTag;
    private Quest[] completedQuests;
    private Quest[] activeQuests;
    private boolean present;
    private final UUID player;

    public PlayerContribution(Player player, Faction faction) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = player.getUniqueId();
        this.factionName = faction.getName();
        this.factionTag = faction.getTag();
        this.present = true;
        this.activeQuests = new Quest[config.getNode("quests", "activeLimit").getInt()];
        this.completedQuests = new Quest[0];
    }

    public PlayerContribution(String player, String factionName, String factionTag) {
        CommentedConfigurationNode config = AFMCorePlugin.getConfig();
        this.player = UUID.fromString(player);
        this.factionName = factionName;
        this.factionTag = Text.of(factionTag);
        this.activeQuests = new Quest[config.getNode("quests", "activeLimit").getInt()];
    }

    public boolean assignQuest(Quest quest) {
        for (int i = 0; i < activeQuests.length; i++) {
            if (activeQuests[i] == null) {
                activeQuests[i] = quest;
                PluginContainer plugin = Sponge.getPluginManager().getPlugin("afmcp").get();
                EventContext eventContext = EventContext.builder()
                        .add(EventContextKeys.PLUGIN, plugin)
                        .add(EventContextKeys.PLAYER_SIMULATED, GameProfile.of(player))
                        .build();
                QuestAssignedEvent questAssignedEvent =
                        new QuestAssignedEventImpl(this, quest, player, Cause.of(eventContext, plugin));
                Sponge.getEventManager().post(questAssignedEvent);

                return !questAssignedEvent.isCancelled();
            }
        }
        return false;
    }

    public void completeQuest(Quest quest) {
        QuestTarget questTarget = quest.getTarget();
        if (questTarget.getProgress() >= questTarget.getCount()) {
            for (int i = 0; i < activeQuests.length; i++) {
                if (activeQuests[i] == quest) {
                    activeQuests[i] = null;
                    completedQuests[i] = quest;
                }
            }
        }
    }

    // ONLY FOR DESERIALIZER!!!
    public void resetCompletedQuests(Quest[] quests) {
        this.completedQuests = quests;
    }

    public Faction getFaction() {
        return EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(factionName);
    }

    public String getFactionName() {
        return factionName;
    }

    public Text getFactionTag() {
        return factionTag;
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
        return String.format("%s - %s\n%s - %s/%s", getPlayer(), isPresent(),
                getFaction(), activeQuests.length, completedQuests.length);
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
