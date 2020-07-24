package ru.allformine.afmcp.listeners;

import com.typesafe.config.ConfigException;
import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.api.events.FactionCreateEvent;
import io.github.aquerr.eaglefactions.api.events.FactionDisbandEvent;
import io.github.aquerr.eaglefactions.api.events.FactionLeaveEvent;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import io.github.aquerr.eaglefactions.common.events.FactionAreaEnterEventImpl;
import io.github.aquerr.eaglefactions.common.events.FactionJoinEventImpl;
import io.github.aquerr.eaglefactions.common.events.FactionLeaveEventImpl;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.scheduler.Task;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PacketChannels;
import ru.allformine.afmcp.quests.PlayerContribution;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class FactionEventListener {
    private static Logger logger = AFMCorePlugin.logger;

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Player player = event.getTargetEntity();

        Optional<Faction> faction = EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), player.getLocation().getChunkPosition());
        sendToPlayer(player, getFactionNameForPlayer(faction.orElse(null), player));
    }

    @Listener
    public void onFactionAreaChange(FactionAreaEnterEventImpl event) {
        sendToPlayer(event.getCreator(), getFactionNameForPlayer(event.getEnteredFaction().orElse(null), event.getCreator()));
    }

    // ============================== //

    private void sendToPlayer(Player player, String string) {
        PacketChannels.FACTIONS.sendTo(player, buf -> buf.writeString(string));
    }

    private String getFactionNameForPlayer(Faction faction, Player player) {
        String factionName = faction == null ? "Общая" : faction.getName();
        String factionColor;

        if (AFMCorePlugin.currentLobby != null && AFMCorePlugin.currentLobby.isPlayerInLobby(player)) {
            factionColor = "§9";
            factionName = "Лобби";
        } else if (factionName.equals("SafeZone") || EagleFactionsPlugin.getPlugin().getConfiguration().getProtectionConfig().getSafeZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = "§d";
            factionName = "SafeZone";
        } else if (factionName.equals("WarZone") || EagleFactionsPlugin.getPlugin().getConfiguration().getProtectionConfig().getWarZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = "§4";
            factionName = "WarZone";
        } else if (faction == null) {
            factionColor = "§2";
        } else {
            if (faction.containsPlayer(player.getUniqueId())) {
                factionColor = "§a";
            } else {
                factionColor = "§6";
            }
        }

        return factionColor + factionName;
    }

    /* ====================================== */
    /*            Quests block
    /*   Every listener has it's own task
    /*   because event's are cancelable and
    /*   have a delay before action happens
    /* ====================================== */

    @Listener
    public void factionJoinEventImpl(FactionJoinEventImpl event) {
        Task task = Task.builder().execute(new FactionJoinDelayTask(event))
                .interval(500, TimeUnit.MILLISECONDS)
                .async()
                .name("Self-Cancelling FJoin Timer Task").submit(
                        Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));
    }

    private static class FactionJoinDelayTask implements Consumer<Task> {
        private final FactionJoinEventImpl event;

        public FactionJoinDelayTask(FactionJoinEventImpl event) {
            this.event = event;
        }

        @Override
        public void accept(Task task) {
            try {
                PlayerContribution p = new PlayerContribution(event.getCreator().getUniqueId(), event.getFaction());
                AFMCorePlugin.questDataManager.updateContribution(p, "a");
                task.cancel();
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Listener
    public void factionLeaveEventImpl(FactionLeaveEventImpl event) {
        Task task = Task.builder().execute(new FactionLeaveDelayTask(event))
                .interval(500, TimeUnit.MILLISECONDS)
                .async()
                .name("Self-Cancelling FLeave Timer Task").submit(
                        Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));
    }

    private static class FactionLeaveDelayTask implements Consumer<Task> {
        private final FactionLeaveEventImpl event;

        public FactionLeaveDelayTask(FactionLeaveEventImpl event) {
            this.event = event;
        }

        @Override
        public void accept(Task task) {
            try {
                PlayerContribution p = AFMCorePlugin.questDataManager.getContribution(event.getCreator().getUniqueId());
                AFMCorePlugin.questDataManager.updateContribution(p, "p");
                task.cancel();
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Listener
    public void factionCreateEvent(FactionCreateEvent event) {
        Task task = Task.builder().execute(new FactionCreateDelayTask(event))
                .interval(500, TimeUnit.MILLISECONDS)
                .async()
                .name("Self-Cancelling FCreate Timer Task").submit(
                        Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));
    }

    private static class FactionCreateDelayTask implements Consumer<Task> {
        private final FactionCreateEvent event;

        public FactionCreateDelayTask(FactionCreateEvent event) {
            this.event = event;
        }

        @Override
        public void accept(Task task) {
            try {
                if (EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(event.getFaction().getName()) != null) {
                    logger.debug("Triggered quest FACTION CREATE");
                    PlayerContribution p = new PlayerContribution(event.getCreator().getUniqueId(), event.getFaction());
                    AFMCorePlugin.questDataManager.updateContribution(p, "c");
                    task.cancel();
                }
            } catch (AssertionError e) {
                logger.warn(e.getMessage());
                task.cancel();
            }
        }
    }

    @Listener
    public void factionDisbandEvent(FactionDisbandEvent event) {
        Task task = Task.builder().execute(new FactionDisbandDelayTask(event))
                .interval(500, TimeUnit.MILLISECONDS)
                .async()
                .name("Self-Cancelling FDisband Timer Task").submit(
                        Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));

    }

    private static class FactionDisbandDelayTask implements Consumer<Task> {
        private final FactionDisbandEvent event;

        public FactionDisbandDelayTask(FactionDisbandEvent event) {
            this.event = event;
        }

        @Override
        public void accept(Task task) {
            if (EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(event.getFaction().getName()) == null) {
                try {
                    if (AFMCorePlugin.questDataManager.getQuestFactions().getQuestFaction(event.getFaction().getName()).isPresent()) {
                        logger.debug("Triggered quest FACTION DISBAND");
                        PlayerContribution p = AFMCorePlugin.questDataManager.getContribution(event.getCreator().getUniqueId());
                        AFMCorePlugin.questDataManager.updateContribution(p, "d");
                    }
                    task.cancel();
                } catch (NullPointerException ignore) {
                    logger.debug("Disbandned faction wasn't present in faction list");
                    task.cancel();
                }

            }
        }
    }

    @Listener
    public void onCommandSend(SendCommandEvent event, @Root Player player) {
        Faction prev = EagleFactionsPlugin.getPlugin().getFactionLogic().
                getFactionByPlayerUUID(player.getUniqueId()).orElse(null);
        if (event.getCommand().equals("f")) {
            if (prev != null) {
                if (player.getUniqueId().equals(prev.getLeader())
                        && event.getArguments().split(" ")[0].equals("rename")) {
                    Task task = Task.builder().execute(new SendCommandDelayTask(event, player, prev.getName()))
                            .interval(500, TimeUnit.MILLISECONDS)
                            .async()
                            .name("Self-Cancelling FRename Timer Task").submit(
                                    Objects.requireNonNull(Sponge.getPluginManager().getPlugin("afmcp").orElse(null)));
                }
                //// TODO: Fix issue #2
                if (event.getArguments().equals("disband")) {
                    EventContext eventContext = EventContext.builder().add(EventContextKeys.OWNER, player)
                            .add(EventContextKeys.PLAYER, player).add(EventContextKeys.CREATOR, player).build();
                    Cause eventCause = Cause.of(eventContext, player, prev);
                    Sponge.getEventManager().post(new FactionDisbandEvent() {

                        @Override
                        public Cause getCause() {
                            return eventCause;
                        }

                        @Override
                        public boolean isCancelled() {
                            return false;
                        }

                        @Override
                        public void setCancelled(boolean cancel) {
                            // It actually is not cancellable in my case lmao
                        }

                        @Override
                        public Player getCreator() {
                            return player;
                        }

                        @Override
                        public Faction getFaction() {
                            return prev;
                        }
                    });
                }
            }
        }
    }

    private static class SendCommandDelayTask implements Consumer<Task> {
        private final SendCommandEvent event;
        private final Player player;
        private final String temp;

        public SendCommandDelayTask(SendCommandEvent event, Player player, String temp) {
            this.event = event;
            this.player = player;
            this.temp = temp;
        }

        @Override
        public void accept(Task task) {
            if (EagleFactionsPlugin.getPlugin().getFactionLogic().getFactionByName(temp) == null) {
                try {
                    String temp = EagleFactionsPlugin.getPlugin().getFactionLogic().
                            getFactionByPlayerUUID(player.getUniqueId()).orElse(null).getName();
                } catch (NullPointerException e) {
                    logger.warn("Rename won't work because player couldn't be found using UUID");
                    event.setCancelled(true);
                }
            }
        }
    }

}