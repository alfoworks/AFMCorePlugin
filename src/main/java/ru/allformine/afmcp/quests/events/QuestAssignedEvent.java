package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.util.UUID;

// Thrown when a quest was assigned
public interface QuestAssignedEvent extends Event, Cancellable {

    UUID getPlayer();

    PlayerContribution getContribution();

    Faction getHost();

    boolean isCancelled();

    void setCancelled(boolean cancel);

    Quest getQuest();
}
