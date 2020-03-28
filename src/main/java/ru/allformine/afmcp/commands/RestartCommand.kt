package ru.allformine.afmcp.commands

import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandException
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.scheduler.Task
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.serializer.TextSerializers
import ru.allformine.afmcp.AFMCorePlugin
import ru.allformine.afmcp.Utils
import java.util.concurrent.TimeUnit

class RestartCommand : AFMCPCommand() {

    override fun execute(src: CommandSource, args: CommandContext): CommandResult {
        if (task != null) {
            if (args.hasAny("c")) {
                reply(src, Text.of("Рестарт был успешно отменем."))

                sendRestartMessage("Перезапуск сервера был отменён.")

                task?.cancel()
                task = null
            } else {
                reply(src, Text.of("Рестарт уже запланирован. Используйте флаг -с для отмены рестарта."))
            }

            return CommandResult.success()
        } else if (args.hasAny("c")) {
            reply(src, Text.of("Не удалось отменить рестарт: нет запланированных рестартов."))

            return CommandResult.success()
        }

        var minutes = 0

        if (args.getOne<Any>("minutes").isPresent) {
            minutes = args.getOne<Any>("minutes").get() as Int
            if (minutes < 1) {
                throw CommandException(Text.of("Кол-во минут не может быть меньше 1."))
            }
        }

        reply(src, Text.of("Сервер будет перезапущен."))
        task = Task.builder()
                .execute(RestartRunnable(minutes, if (minutes == 0) 10 else 60))
                .interval(1, TimeUnit.MINUTES)
                .submit(AFMCorePlugin.instance)

        return CommandResult.success()
    }

    companion object {
        var task: Task? = null

        fun sendRestartMessage(message: String) {
            Sponge.getServer().broadcastChannel.send(TextSerializers.FORMATTING_CODE.deserialize("&7-&2G &cРестарт&f: $message"))
        }
    }

    override fun getName(): String {
        return "AFMRestart"
    }

    override fun getColor(): TextColor {
        return TextColors.RED
    }

    class RestartRunnable constructor(private var minutes: Int, seconds: Int) : Runnable {
        private var seconds = 60

        override fun run() {
            if (minutes > 0) {
                sendRestartMessage(String.format("Сервер будет перезапущен через %s %s", minutes, pluralize(minutes, "минуту", "минуты", "минут")))
                minutes--
            }

            if (minutes == 0) {
                task = Task.builder().execute(Runnable {
                    if (seconds == 30 || seconds in 1..10) {
                        sendRestartMessage(String.format("Сервер будет перезапущен через %s %s", seconds, pluralize(seconds, "секунду", "секунды", "секунд")))
                    } else if (seconds == 0) {
                        sendRestartMessage("Сервер перезапускается!")
                        Utils.afmRestart()
                    }

                    seconds--
                }).interval(1, TimeUnit.SECONDS).submit(AFMCorePlugin.instance)
            }
        }

        companion object {
            private fun pluralize(number: Int, nomSing: String, genSing: String, genPl: String): String {
                 // val numberString = number.toString()
                // val lastDigit = numberString.substring(numberString.length - 1).toInt()
                // val lastTwoDigits = if (numberString.length > 1) numberString.substring(numberString.length - 2).toInt() else lastDigit
                val lastDigit = number % 10
                val lastTwoDigits = number % 100
                return when {
                    lastTwoDigits in 11..19 -> genPl
                    lastDigit == 1 -> nomSing
                    lastDigit in 2..4 -> genSing
                    else -> genPl
                }
            }
        }

        init {
            this.seconds = seconds
        }
    }
}