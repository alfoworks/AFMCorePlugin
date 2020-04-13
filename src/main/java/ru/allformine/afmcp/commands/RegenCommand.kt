package ru.allformine.afmcp.commands

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.world.World

class RegenCommand : AFMCPCommand() {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (src !is Player) {
            replyString(src, "Вы не можете выполнить это от консоли.")
            return CommandResult.success()
        }

        val world: World = src.world
        world.regenerateChunk(src.location.chunkPosition)

        replyString(src, "Чанк был отрегенерирован!")

        return CommandResult.success()
    }

    override fun getName(): String {
        return "Regen";
    }

    override fun getColor(): TextColor {
        return TextColors.GOLD;
    }
}