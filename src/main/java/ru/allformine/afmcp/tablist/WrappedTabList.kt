package ru.allformine.afmcp.tablist

import org.spongepowered.api.entity.living.player.Player


class WrappedTabList {
    private val entries = ArrayList<WrappedTabListEntry>()

    fun removeEntry(player: Player) {
        entries.removeIf { it.uuid == player.uniqueId }
    }

    fun addEntry(player: Player) {
        if(entries.any { it.uuid == player.uniqueId }) return
        entries.add(WrappedTabListEntry(player))
    }

    fun clearEntries() {
        entries.clear()
    }

    fun sortEntries() {
        entries.sortWith(compareBy<WrappedTabListEntry> { it.priority }.thenBy {it.name})
    }

}