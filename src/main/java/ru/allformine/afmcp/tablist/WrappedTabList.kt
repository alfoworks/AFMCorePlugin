package ru.allformine.afmcp.tablist

import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.tab.TabListEntry


object WrappedTabList {
    private val entries = ArrayList<WrappedTabListEntry>()

    fun removeEntry(player: Player) { entries.removeIf { it.uuid == player.uniqueId } }
    fun clearEntries() { entries.clear() }
    fun sortEntries() { entries.sortWith(compareBy<WrappedTabListEntry> { it.priority }.thenBy { it.name }) }

    fun addEntry(player: Player) {
        if(entries.any { it.uuid == player.uniqueId }) return
        entries.add(WrappedTabListEntry(player))
    }

    fun writeAll() {
        val nativeEntries = ArrayList<TabListEntry>()
        entries.forEach {
            nativeEntries.add(TabListEntry.builder()
                    .displayName(it.name)
                    .gameMode(it.gameMode.get())
                    .latency(it.latency)
                    .profile(it.player.profile)
                    .build())
        }

        for(player in Sponge.getServer().onlinePlayers) {
            val tablist = player.tabList
            tablist.entries.forEach {
                tablist.removeEntry(it.profile.uniqueId)
            }
            nativeEntries.forEach {
                tablist.addEntry(it)
            }
        }
    }

}