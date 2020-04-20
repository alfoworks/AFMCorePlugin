package ru.allformine.afmcp.listeners;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.api.events.FactionCreateEvent;
import io.github.aquerr.eaglefactions.api.events.FactionDisbandEvent;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import io.github.aquerr.eaglefactions.common.events.FactionAreaEnterEventImpl;
import io.github.aquerr.eaglefactions.common.events.FactionJoinEventImpl;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PacketChannels;
import ru.allformine.afmcp.quests.PlayerContribution;

import java.util.Optional;

public class FactionEventListener {
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

    // ============================== //

    @Listener
    public void factionJoinEventImpl(FactionJoinEventImpl event) {
        PlayerContribution p = new PlayerContribution(event.getCreator(), event.getFaction());
        AFMCorePlugin.questDataManager.updateContribution(p, "a");
    }

    @Listener
    public void factionLeaveEventImpl(FactionJoinEventImpl event) {
        PlayerContribution p = AFMCorePlugin.questDataManager.getContribution(event.getCreator().getUniqueId());
        AFMCorePlugin.questDataManager.updateContribution(p, "u");
    }

    @Listener
    public void factionCreateEvent(FactionCreateEvent event) {
        PlayerContribution p = new PlayerContribution(event.getCreator(), event.getFaction());
        AFMCorePlugin.questDataManager.updateContribution(p, "c");
    }

    @Listener
    public void factionDisbandEvent(FactionDisbandEvent event) {
        PlayerContribution p = AFMCorePlugin.questDataManager.getContribution(event.getCreator().getUniqueId());
        AFMCorePlugin.questDataManager.updateContribution(p, "d");
    }

    @Listener
    public void onCommandSend(SendCommandEvent event, @Root Player player) {
        if (event.getCommand().equals("f rename")) {
            PlayerContribution p = AFMCorePlugin.questDataManager.getContribution(player.getUniqueId());
            AFMCorePlugin.questDataManager.updateContribution(p, String.format("r%s", event.getArguments()));
        }
    }
}
