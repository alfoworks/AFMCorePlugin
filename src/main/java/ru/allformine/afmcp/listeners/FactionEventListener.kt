package ru.allformine.afmcp.listeners

import com.flowpowered.math.vector.Vector3i
import io.github.aquerr.eaglefactions.api.entities.Faction
import io.github.aquerr.eaglefactions.common.EagleFactionsPlugin
import io.github.aquerr.eaglefactions.common.events.FactionAreaEnterEventImpl
import io.github.aquerr.eaglefactions.common.events.FactionClaimEventImpl
import io.github.aquerr.eaglefactions.common.events.FactionUnclaimEventImpl
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent.Join
import org.spongepowered.api.network.ChannelBuf
import ru.allformine.afmcp.AFMCorePlugin
import ru.allformine.afmcp.PacketChannels

class FactionEventListener {
    @Listener
    fun onPlayerJoin(event: Join) {
        val player = event.targetEntity
        val faction = EagleFactionsPlugin.getPlugin().factionLogic.getFactionByChunk(player.world.uniqueId, player.location.chunkPosition)

        sendToPlayer(player, getFactionNameForPlayer(faction.orElse(null), player))
    }

    @Listener
    fun onFactionAreaChange(event: FactionAreaEnterEventImpl) {
        sendToPlayer(event.creator, getFactionNameForPlayer(event.enteredFaction.orElse(null), event.creator))
    }

    @Listener
    fun onFactionClaim(event: FactionClaimEventImpl) {
        getAllPlayersInChunk(event.chunkPosition).forEach {
            sendToPlayer(it, getFactionNameForPlayer(event.faction, it))
        }
    }

    @Listener
    fun onFactionUnclaim(event: FactionUnclaimEventImpl) {
        getAllPlayersInChunk(event.chunkPosition).forEach {
            sendToPlayer(it, getFactionNameForPlayer(event.faction, it))
        }
    }

    // ============================== //
    private fun sendToPlayer(player: Player, string: String) {
        PacketChannels.FACTIONS.sendTo(player) { buf: ChannelBuf -> buf.writeString(string) }
    }

    private fun getFactionNameForPlayer(faction: Faction?, player: Player): String {
        var factionName = if (faction == null) "Общая" else faction.name
        val factionColor: String

        if (EagleFactionsPlugin.ADMIN_MODE_PLAYERS.contains(player.uniqueId)) {
            factionColor = "§4"
        } else if (AFMCorePlugin.currentLobby != null && AFMCorePlugin.currentLobby.isPlayerInLobby(player)) {
            factionColor = "§9"
            factionName = "Лобби"
        } else if (factionName == "SafeZone" || EagleFactionsPlugin.getPlugin().configuration.protectionConfig.safeZoneWorldNames.contains(player.world.name)) {
            factionColor = "§d"
            factionName = "SafeZone"
        } else if (factionName == "WarZone" || EagleFactionsPlugin.getPlugin().configuration.protectionConfig.warZoneWorldNames.contains(player.world.name)) {
            factionColor = "§c"
            factionName = "WarZone"
        } else if (faction == null) {
            factionColor = "§2"
        } else {
            factionColor = if (faction.containsPlayer(player.uniqueId)) {
                "§a"
            } else {
                "§6"
            }
        }

        return factionColor + factionName
    }

    private fun getAllPlayersInChunk(pos: Vector3i): List<Player> {
        return Sponge.getServer().onlinePlayers.filter { it.location.chunkPosition == pos }
    }
}