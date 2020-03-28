package ru.allformine.afmcp.commands

import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import ru.allformine.afmcp.Messaging

class MessageCommand : AFMCPCommand() {
    @Throws(CommandException::class)
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (!args.getOne<String>("type").isPresent || !args.getOne<String>("message").isPresent) {
            throw CommandException(Text.of("Недостаточно аргументов!"))
        }

        val message = args.getOne<String>("message").get()

        if (message.length > 40) {
            reply(src, Text.of("Максимальная длина сообщения - 40 символов."))

            return CommandResult.success()
        }

        val type: Messaging.MessageType

        type = try {
            Messaging.MessageType.valueOf(args.getOne<String>("type").get().toUpperCase())
        } catch (ignored: IllegalArgumentException) {
            reply(src, Text.of("Неверный тип сообщения! Правильные типы:"))

            for (messageType in Messaging.MessageType.values()) {
                src.sendMessage(Text.of(messageType))
            }

            return CommandResult.success()
        }

        val player = args.getOne<Player>("player").orElse(null);

        Messaging.sendMessage(player, message, type)
        reply(src, Text.of("Сообщение было отправлено всем игрокам."))

        return CommandResult.success()
    }

    override fun getName(): String {
        return "Messaging"
    }

    override fun getColor(): TextColor {
        return TextColors.DARK_BLUE
    }
}