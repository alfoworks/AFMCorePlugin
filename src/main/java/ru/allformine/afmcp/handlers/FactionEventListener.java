package ru.allformine.afmcp.handlers;

import com.flowpowered.math.vector.Vector3i;
import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.entities.Faction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.PacketChannels;

import java.util.Arrays;
import java.util.Optional;

public class FactionEventListener {
    @Listener
    public void onPlayerMove(MoveEntityEvent event, @Root Player player) {
        Vector3i oldChunk = event.getFromTransform().getLocation().getChunkPosition();
        Vector3i newChunk = event.getToTransform().getLocation().getChunkPosition();

        Optional<Faction> oldFaction = EagleFactions.getPlugin().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), oldChunk);
        Optional<Faction> newFaction = EagleFactions.getPlugin().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), newChunk);

        if (newFaction.orElse(null) == oldFaction.orElse(null)) return;

        Optional<Faction> faction = EagleFactions.getPlugin().getFactionLogic().getFactionByChunk(event.getTargetEntity().getWorld().getUniqueId(), newChunk);
        String factionName = getFactionNameForPlayer(faction, player);

        sendToPlayer(player, factionName);
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        Vector3i chunk = event.getTargetEntity().getLocation().getChunkPosition();
        Optional<Faction> faction = EagleFactions.getPlugin().getFactionLogic().getFactionByChunk(event.getTargetEntity().getWorld().getUniqueId(), chunk);
        String factionName = getFactionNameForPlayer(faction, event.getTargetEntity());

        sendToPlayer(event.getTargetEntity(), factionName);
    }

    @Listener
    public void onPlayerCommand(SendCommandEvent event) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        String allCommand = event.getCommand() + " " + event.getArguments();
        Player player = (Player) event.getSource();

        if (isClaimUpdateCommand(allCommand)) {
            Vector3i chunk = player.getLocation().getChunkPosition();
            Optional<Faction> faction = EagleFactions.getPlugin().getFactionLogic().getFactionByChunk(player.getWorld().getUniqueId(), chunk);
            String factionName = getFactionNameForPlayer(faction, player);

            sendToPlayer(player, factionName);
        }
    }

    // ============================== //

    private void sendToPlayer(Player player, String string) {
        PacketChannels.FACTIONS.sendTo(player, buf -> buf.writeString(string));
    }

    private String getFactionNameForPlayer(Optional<Faction> faction, Player player) {
        String factionName = faction.isPresent() ? faction.get().getName() : "Общая";
        String factionColor;

        if (factionName.equals("SafeZone") || EagleFactions.getPlugin().getConfiguration().getConfigFields().getSafeZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = "§d";
            factionName = "SafeZone";
        } else if (factionName.equals("WarZone") || EagleFactions.getPlugin().getConfiguration().getConfigFields().getWarZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = "§4";
            factionName = "WarZone";
        } else if (!factionName.equals("Общая") && faction.get().containsPlayer(player.getUniqueId())) {
            factionColor = "§a";
        } else if (!factionName.equals("Общая") && !faction.get().containsPlayer(player.getUniqueId())) {
            factionColor = "§6";
        } else {
            factionColor = "§2";
        }

        return factionColor + factionName;
    }

    private boolean isClaimUpdateCommand(String command) {
        String[] commands = new String[]{"f claim", "f unclaim", "f unclaimall", "f disband", "f leave", "f join", "f rename", "f squareclaim"};

        return Arrays.asList(commands).contains(command);
    }
}
