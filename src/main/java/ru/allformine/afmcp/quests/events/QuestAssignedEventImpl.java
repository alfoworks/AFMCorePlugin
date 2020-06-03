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
    private final UUID player;
    private final PlayerContribution playerContribution;
    private final Faction host;
    private final Quest quest;
    private final Cause cause;
    private boolean cancelled;

    public QuestAssignedEventImpl(PlayerContribution contribution, Quest quest, UUID player, Cause cause) {
        this.player = player;
        this.playerContribution = contribution;
        this.host = playerContribution.getFaction();
        this.quest = quest;
        this.cause = cause;
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
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public Quest getQuest() {
        return quest;
    }

    @Override
    public Cause getCause() {
        return cause;
    }

    @Override
    public String toString() {
        return "QuestAssignedEvent";
    }
}
