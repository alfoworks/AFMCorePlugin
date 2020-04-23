package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

public interface PreQuestSelectionEvent extends Event {

    Player getPlayer();

    PlayerContribution getContribution();

    Faction getHost();

    Quest getQuest();

}
