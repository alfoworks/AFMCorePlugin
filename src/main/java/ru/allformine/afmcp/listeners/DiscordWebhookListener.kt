package ru.allformine.afmcp.listeners

import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.advancement.AdvancementEvent
import org.spongepowered.api.event.command.SendCommandEvent
import org.spongepowered.api.event.entity.DestructEntityEvent.Death
import org.spongepowered.api.event.filter.cause.First
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.event.network.ClientConnectionEvent.Join
import ru.allformine.afmcp.AFMCorePlugin
import ru.allformine.afmcp.net.api.Webhook
import ru.allformine.afmcp.net.api.Webhook.TypePlayerMessage

class DiscordWebhookListener {
    @Listener(order = Order.POST)
    fun onPlayerJoin(event: Join) {
        val type = if (!event.isMessageCancelled) if (event.targetEntity.hasPlayedBefore()) TypePlayerMessage.JOINED_SERVER else TypePlayerMessage.JOINED_FIRST_TIME else TypePlayerMessage.STAFF_JOINED_SERVER

        Webhook.sendPlayerMessage(type, event.targetEntity)
    }

    @Listener(order = Order.POST)
    fun onPlayerQuit(event: ClientConnectionEvent.Disconnect) {
        if (AFMCorePlugin.serverRestart) return

        Webhook.sendPlayerMessage(if (!event.isMessageCancelled) TypePlayerMessage.LEFT_SERVER else TypePlayerMessage.STAFF_LEFT_SERVER, event.targetEntity)
    }

    @Listener(order = Order.POST)
    fun onCommandSend(event: SendCommandEvent, @First player: Player) {
        Webhook.sendPlayerMessage(TypePlayerMessage.COMMAND, player, event.command + " " + event.arguments)
    }

    @Listener(order = Order.POST)
    fun onAdvancement(event: AdvancementEvent.Grant) {
        if (!event.advancement.name.startsWith("recipes_") && !event.isMessageCancelled) {
            Webhook.sendPlayerMessage(TypePlayerMessage.EARNED_ADVANCEMENT,
                    event.targetEntity,
                    event.advancement.name
            )
        }
    }

    @Listener(order = Order.POST)
    fun onDeath(event: Death) {
        if (event.targetEntity is Player && !event.isMessageCancelled) {
            Webhook.sendPlayerMessage(TypePlayerMessage.DIED,
                    event.targetEntity as Player,
                    event.message.toPlain()
            )
        }
    }
}