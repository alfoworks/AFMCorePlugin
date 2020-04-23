package ru.allformine.afmcp.listeners;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.quests.events.PreQuestSelectionEvent;
import ru.allformine.afmcp.quests.events.QuestAssignedEvent;
import ru.allformine.afmcp.quests.events.QuestCompletedEvent;

import java.util.Objects;

public class QuestEventListener {
    private final Logger logger = AFMCorePlugin.logger;

    @Listener
    public void ChangeInventoryEvent(ChangeInventoryEvent event) {
        if (Objects.equals(event.getTargetInventory().getInventoryProperty(InventoryTitle.class)
                .orElse(InventoryTitle.of(Text.of(""))).getValue(), Text.of(TextColors.YELLOW, "Quest Menu"))) {
            Sponge.getEventManager().post(PreQuestSelectionEvent, );
            event.setCancelled(true);
        }
    }

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
