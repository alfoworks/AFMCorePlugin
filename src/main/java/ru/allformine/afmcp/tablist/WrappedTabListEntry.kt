package ru.allformine.afmcp.tablist

import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.text.Text
import ru.allformine.afmcp.AFMCorePlugin
import java.util.*


class WrappedTabListEntry(player: Player) {
    val lpUser = AFMCorePlugin.instance.api.userManager.getUser(player.uniqueId)
    val name = Text.of("123")
    var latency = player.connection.latency
    val uuid: UUID = player.uniqueId
    val gameMode: Value<GameMode> = player.gameMode()
    lateinit var header: String
    lateinit var footer: String

//    fun WrappedTabListEntry(player: Player) {
//        header = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListHeader)
//        footer = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListFooter)
//        generateDynamicStuff(player)
//    }
}