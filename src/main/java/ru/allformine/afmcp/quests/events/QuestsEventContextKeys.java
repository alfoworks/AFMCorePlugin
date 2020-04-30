package ru.allformine.afmcp.quests.events;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import org.spongepowered.api.event.cause.EventContextKey;
import ru.allformine.afmcp.quests.PlayerContribution;
import ru.allformine.afmcp.quests.Quest;

import java.util.UUID;

public class QuestsEventContextKeys {
    public static final EventContextKey<Quest> QUEST = createFor(Quest.class);

    public static final EventContextKey<PlayerContribution> PLAYER_CONTRIBUTION = createFor(PlayerContribution.class);

    public static final EventContextKey<Faction> FACTION = createFor(Faction.class);

    public static final EventContextKey<java.util.UUID> UUID = createFor(UUID.class);

    private static <T> EventContextKey<T> createFor(Class<T> tClass) {
        return EventContextKey.builder(tClass).build();
    }
}
