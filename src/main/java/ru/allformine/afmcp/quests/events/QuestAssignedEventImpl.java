package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.util.Optional;
import java.util.UUID;

public class QuestAssignedEventImpl implements QuestAssignedEvent {
    private UUID player;
    private PlayerContribution playerContribution;
    private Faction host;
    private Quest quest;
    private Cause cause;

    public QuestAssignedEventImpl(Cause cause) {
        Optional<PlayerContribution> playerContribution = cause.first(PlayerContribution.class);
        Optional<Quest> quest = cause.first(Quest.class);
        if (playerContribution.isPresent() && quest.isPresent()) {
            this.player = playerContribution.get().getPlayer();
            this.playerContribution = playerContribution.get();
            this.host = playerContribution.get().getFaction();
            this.quest = quest.get();
            this.cause = cause;
        } else {
            throw new IllegalArgumentException("Wrong QuestAssignedEvent cause");
        }
    }

    @Override
    public UUID getPlayer() {
        return player;
    }

    @Override
    public PlayerContribution getContribution() {
        return playerContribution;
    }

    @Override
    public Faction getHost() {
        return host;
    }

    @Override
    public boolean isCancelled() {
        return quest == null;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.quest = null;
    }

    @Override
    public Quest getQuest() {
        return quest;
    }

    @Override
    public Cause getCause() {
        return cause;
    }
}
