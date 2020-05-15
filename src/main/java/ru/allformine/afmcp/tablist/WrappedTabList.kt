package ru.allformine.afmcp.tablist

import org.spongepowered.api.Sponge
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.tab.TabListEntry
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.format.TextColors
import java.util.*


object WrappedTabList {
    val entries = ArrayList<WrappedTabListEntry>()

    fun removeEntry(player: Player) {
        entries.removeIf { it.uuid == player.uniqueId }
    }

    fun clearEntries() {
        entries.clear()
    }

    fun sortEntries() {
        entries.sortWith(compareBy<WrappedTabListEntry> { it.priority }.thenBy { it.name })
    }

    fun addEntry(player: Player) {
        if (entries.any { it.uuid == player.uniqueId }) return
        entries.add(WrappedTabListEntry(player))
    }

    fun writeAll() {
        for (player in Sponge.getServer().onlinePlayers) {
            ArrayList(player.tabList.entries).forEach {
                player.tabList.removeEntry(it.profile.uniqueId)
            }
        }
        for (entry in entries) {
            entry.setHeaderAndFooter()
            Sponge.getServer().onlinePlayers.forEach {
                val seeVanished = entry.player.get(Keys.VANISH).orElse(false) && !it.hasPermission("afmvanish.vanish.staff")
                if (seeVanished) return@forEach

                val nativeEntry = TabListEntry.builder()
                        .list(it.tabList)
                        .displayName(Text.of((if (seeVanished) Text.of(TextColors.DARK_AQUA, "[V]", TextColors.RESET, " ") else Text.EMPTY), entry.name))
                        .gameMode(entry.gameMode.get())
                        .latency(entry.latency)
                        .profile(entry.player.profile)
                        .build()
                it.tabList.addEntry(nativeEntry)
            }
        }
    }

}