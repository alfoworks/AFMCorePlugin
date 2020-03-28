package ru.allformine.afmcp.tablist

import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import ru.allformine.afmcp.PluginConfig
import java.util.*


class WrappedTabListEntry(val player: Player) {
    // private val lpUser = AFMCorePlugin.luckPerms.userManager.getUser(player.uniqueId)
    private val tablist = player.tabList
    // private val location = player.location

    var header: LiteralText = Text.of("Header")
    var footer: LiteralText = Text.of("Footer")

    val priority: Int = PluginConfig.tablistSorting.childrenList
            .find { player.hasPermission("group.{}".format(it.string)) }
            ?.let { PluginConfig.tablistSorting.childrenList.indexOf(it) } ?: 2147483647
    val name: LiteralText = Text.of(player.name)
    val latency = player.connection.latency
    val uuid: UUID = player.uniqueId
    val gameMode: Value<GameMode> = player.gameMode()

    fun setHeaderAndFooter() {
        tablist.setHeaderAndFooter(header, footer)
    }

    override fun toString(): String {
        return "%s@mine.alfo.ws/?priority=%s".format(name.toString(), priority)
    }

    /* private fun generateDynamicStuff(player: Player) {
        val playerLocation: Location = player.location
        header += ChatColor.translateAlternateColorCodes('&', String.format(PluginConfig.tabListOnlineCount, Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers()))
        footer += ChatColor.translateAlternateColorCodes('&', String.format(PluginConfig.tabListCoordinates, playerLocation.getBlockX(), playerLocation.getBlockY(), playerLocation.getBlockZ()))
    } */

//    fun WrappedTabListEntry(player: Player) {
//        header = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListHeader)
//        footer = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListFooter)
//        generateDynamicStuff(player)
//    }
}