package ru.allformine.afmcp.listeners;

import io.github.aquerr.eaglefactions.api.entities.Faction;
import io.github.aquerr.eaglefactions.api.events.FactionAreaEnterEvent;
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import ru.allformine.afmcp.PacketChannels;

import java.util.Optional;

public class FactionEventListener {
    @Listener
    public void onFactionAreaChange(FactionAreaEnterEvent event) {
        sendToPlayer(event.getCreator(), getFactionNameForPlayer(event.getEnteredFaction(), event.getCreator()));
    }

    // ============================== //

    private void sendToPlayer(Player player, String string) {
        PacketChannels.FACTIONS.sendTo(player, buf -> buf.writeString(string));
    }

    private String getFactionNameForPlayer(Optional<Faction> faction, Player player) {
        String factionName = faction.isPresent() ? faction.get().getName() : "Общая";
        String factionColor;

        if (factionName.equals("SafeZone") || EagleFactionsPlugin.getPlugin().getConfiguration().getConfigFields().getSafeZoneWorldNames().contains(player.getWorld().getName())) {
            factionColor = "§d";
            factionName = "SafeZone";
        } else if (factionName.equals("WarZone") || EagleFactionsPlugin.getPlugin().getConfiguration().getConfigFields().getWarZoneWorldNames().contains(player.getWorld().getName())) {
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
}
