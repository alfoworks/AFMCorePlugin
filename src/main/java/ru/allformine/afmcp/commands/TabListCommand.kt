package ru.allformine.afmcp.commands

import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import ru.allformine.afmcp.tablist.WrappedTabList

class TabListCommand : AFMCPCommand() {
    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult? {
        var replyStr = "Игроки: "
        WrappedTabList.entries.forEach { replyStr += "%s ".format(it.toString()) }
        replyString(src, replyStr)
        return CommandResult.success()
    }

    override fun getName(): String? {
        return "TabListDebug"
    }
}