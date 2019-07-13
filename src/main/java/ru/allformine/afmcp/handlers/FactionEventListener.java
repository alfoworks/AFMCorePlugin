package ru.allformine.afmcp.handlers;

import com.flowpowered.math.vector.Vector3i;
import io.github.aquerr.eaglefactions.EagleFactions;
import io.github.aquerr.eaglefactions.entities.Faction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.PacketChannels;

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

    private void sendToPlayer(Player player, String string) {
        PacketChannels.FACTIONS.sendTo(player, buf -> buf.writeString(string));
    }

    private String getFactionNameForPlayer(Optional<Faction> faction, Player player) {
        String factionName = faction.isPresent() ? faction.get().getName() : "Wilderness";
        TextColor factionColor = TextColors.DARK_GREEN;

        if (factionName.equals("SafeZone") || EagleFactions.getPlugin().getConfiguration().getConfigFields().getSafeZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = TextColors.LIGHT_PURPLE;
        } else if (factionName.equals("WarZone") || EagleFactions.getPlugin().getConfiguration().getConfigFields().getWarZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = TextColors.RED;
        } else if (!factionName.equals("Wilderness") && faction.get().containsPlayer(player.getUniqueId())) {
            factionColor = TextColors.GREEN;
        } else {
            factionColor = TextColors.GOLD;
        }

        return Text.builder().append(Text.of(factionName)).color(factionColor).toText().toPlain();
    }
}
