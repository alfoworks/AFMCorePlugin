package ru.allformine.afmcp.commands

import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import ru.allformine.afmcp.AFMCorePlugin
import ru.allformine.afmcp.net.api.Eco
import ru.allformine.afmcp.net.api.Webhook

class VipCommand : AFMCPCommand() {
    private val configNode: ConfigurationNode = AFMCorePlugin.getConfig()
    private val vips = configNode.getNode("vips").childrenMap

    override fun execute(source: CommandSource, args: CommandContext): CommandResult {
        val vipToBuy = args.getOne<String>("selectedVip").orElse("list")

        if (vipToBuy.equals("list", ignoreCase = true)) {
            reply(source, Text.of("Список привилегий:"))

            for ((_, value) in vips) {
                val cost = value.getNode("cost").int.toString()
                val fullName = value.getNode("fullName").string

                source.sendMessage(TextTemplate.of("    ", color, fullName, TextColors.WHITE, " - ", color, cost, " токенов").toText())
            }

            reply(source, Text.of("Для покупки любой из этих привилегий, напишите /vip <имя>."))
            return CommandResult.success()
        }

        if (source is Player) {
            val vipNode = configNode.getNode("vips", vipToBuy.toLowerCase())

            if (!vipNode.isVirtual) {
                val cost = vipNode.getNode("cost").int
                val eco = Eco(source.getName())
                val balance = eco.balance

                if (balance.isPresent) {
                    if (balance.asInt < cost) {
                        reply(source, Text.of("Недостаточно токенов для покупки."))

                        return CommandResult.success()
                    }
                } else {
                    reply(source, Text.of("Произошла неизвестная ошибка."))

                    println("Can't get balance for player " + source.getName())

                    return CommandResult.success()
                }
                val success = eco.decrease(cost)

                if (!success) {
                    reply(source, Text.of("Произошла неизвестная ошибка."))

                    println("Can't decrease balance for player " + source.getName())

                    return CommandResult.success()
                }

                Sponge.getCommandManager().process(Sponge.getServer().console, String.format("setvip %s %s %s",
                        source.getName(),
                        vipToBuy.toLowerCase(),
                        vipNode.getNode("period").int
                ))

                reply(source, Text.of("Привилегия успешно приобретена."))

                Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.BOUGHT_VIP, source, source.name, vipNode.getNode("fullName").string
                        ?: "Nothing")
            } else {
                reply(source, Text.of("Данная привилегия не найдена, введите команду /vip для списка привилегий."))
            }

            return CommandResult.success()
        } else {
            reply(source, Text.of("Данную команду может выполнить только игрок."))
        }

        return CommandResult.success()
    }

    override fun getName(): String {
        return "AFMEco"
    }

    override fun getColor(): TextColor {
        return TextColors.LIGHT_PURPLE
    }
}