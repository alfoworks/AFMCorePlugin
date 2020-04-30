package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.util.UUID;

public class QuestAssignedEventImpl implements QuestAssignedEvent {
    private UUID player;
    private PlayerContribution playerContribution;
    private Faction host;
    private Quest quest;
    private Cause cause;

    public QuestAssignedEventImpl(Cause cause) {
        EventContext context = cause.getContext();
        if (context.get(QuestsEventContextKeys.QUEST).isPresent()) {
            this.player = context.get(QuestsEventContextKeys.UUID).orElse(null);
            this.playerContribution = context.get(QuestsEventContextKeys.PLAYER_CONTRIBUTION).orElse(null);
            this.host = context.get(QuestsEventContextKeys.FACTION).orElse(null);
            this.quest = context.get(QuestsEventContextKeys.QUEST).orElse(null);
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
