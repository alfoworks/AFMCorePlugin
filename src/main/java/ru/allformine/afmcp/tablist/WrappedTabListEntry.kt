package ru.allformine.afmcp.tablist

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.text.LiteralText
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import ru.allformine.afmcp.PluginConfig
import java.util.*


class WrappedTabListEntry(val player: Player) {
    // private val lpUser = AFMCorePlugin.luckPerms.userManager.getUser(player.uniqueId)
    private val tablist = player.tabList
    // private val location = player.location

    var header: Text = Text.of("")
    var footer: Text = Text.of("")

    val priority: Int = PluginConfig.tablistSorting.childrenList
            .find { player.hasPermission("group.%s".format(it.string)) }
            ?.let { PluginConfig.tablistSorting.childrenList.indexOf(it) } ?: 2147483647
    private val prefix = player.getOption("prefix").orElse("")!!
    val name: LiteralText = Text.of(prefix + " " + player.name)
    val latency = player.connection.latency
    val uuid: UUID = player.uniqueId
    val gameMode: Value<GameMode> = player.gameMode()

    fun setHeaderAndFooter() {
        generateDynamicStuff(player)
        tablist.setHeaderAndFooter(header, footer)
    }

    override fun toString(): String {
        return "%s@mine.alfo.ws/?priority=%s".format(player.name, priority)
    }

    private fun generateDynamicStuff(player: Player) {
        val playerLocation = player.location

        header = TextSerializers.FORMATTING_CODE.deserialize(PluginConfig.tabListHeader + "\n" +
                    String.format(PluginConfig.tabListOnlineCount,
                            Sponge.getServer().onlinePlayers.size, Sponge.getServer().maxPlayers))
        footer = TextSerializers.FORMATTING_CODE.deserialize(PluginConfig.tabListFooter + "\n" +
                String.format(PluginConfig.tabListCoordinates, playerLocation.blockX, playerLocation.blockY, playerLocation.blockZ))
    }

//    fun WrappedTabListEntry(player: Player) {
//        header = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListHeader)
//        footer = ChatColor.translateAlternateColorCodes('&', PluginConfig.tabListFooter)
//        generateDynamicStuff(player)
//    }
}