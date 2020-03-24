package ru.allformine.afmcp.commands

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.text.Text

class KotlinTestCommand: AFMCPCommand() {
    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        src.sendMessage(Text.of("Kotlin test ok"))
        return CommandResult.success()
    }
}