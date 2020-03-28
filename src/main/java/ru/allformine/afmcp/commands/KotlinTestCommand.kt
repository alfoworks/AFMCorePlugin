package ru.allformine.afmcp.commands

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.text.Text
// Так блять, ревизия на котлин
class KotlinTestCommand : AFMCPCommand() { // Ну тут вроде понятно все еще.. Хотя стоп. Нахуй тут скобки?
    override fun execute(src: CommandSource, args: CommandContext): CommandResult { // У меня питоновские флешбеки. А кто придумал называть метод весельем*
        src.sendMessage(Text.of("Kotlin test ok")) // То есть нам не нужны точки с запятой? Ууу, ебать
        return CommandResult.success() // Ну ок, это было просто
    }
}