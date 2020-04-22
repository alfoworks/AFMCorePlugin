package ru.allformine.afmcp.listeners;

import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.events.QuestAssignedEvent;
import ru.allformine.afmcp.quests.events.QuestCompletedEvent;

public class QuestEventListener {
    private final Logger logger = AFMCorePlugin.logger;


    // Update JSON data with event provided information
    @Listener
    public void QuestAssignedEvent(QuestAssignedEvent event) {
        AFMCorePlugin.questDataManager.updateContribution(event.getContribution(), "u");
    }

    @Listener
    public void QuestCompletedEvent(QuestCompletedEvent event) {
        AFMCorePlugin.questDataManager.updateContribution(event.getContribution(), "u");
    }
}
