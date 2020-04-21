package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import ru.allformine.afmcp.quests.Quest;

// Thrown when a quest complete
public interface QuestCompletedEvent extends Event, Cancellable {

    Player getPlayer();

    Faction getHost();

    boolean isCancelled();

    void setCancelled(boolean cancel);

    Object getType(String type);

    Quest getQuest();
}
