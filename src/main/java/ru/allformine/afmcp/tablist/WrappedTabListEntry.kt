package ru.allformine.afmcp.tablist

import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.text.Text
import ru.allformine.afmcp.AFMCorePlugin
import java.util.*


class WrappedTabListEntry(player: Player) {
    private val lpUser = AFMCorePlugin.luckPerms.userManager.getUser(player.uniqueId)
    val priority = lpUser?.primaryGroup?.get(0)?.toByte()?.toInt() ?: 0
    val name = Text.of("1234")
    var latency = player.connection.latency
    val uuid: UUID = player.uniqueId
    val gameMode: GameMode = player.gameMode().get()
    lateinit var header: String
    lateinit var footer: String

//    fun WrappedTabListEntry(player: Player) {
//        header = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListHeader)
//        footer = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListFooter)
//        generateDynamicStuff(player)
//    }
}