package ru.allformine.afmcp.tablist

import org.spongepowered.api.Sponge
import ru.allformine.afmcp.tablist.WrappedTabList.addEntry
import ru.allformine.afmcp.tablist.WrappedTabList.clearEntries
import ru.allformine.afmcp.tablist.WrappedTabList.sortEntries
import ru.allformine.afmcp.tablist.WrappedTabList.writeAll

class UpdateTask : Runnable {
    override fun run() {
        clearEntries()
        for (player in Sponge.getServer().onlinePlayers) {
            addEntry(player)
        }
        sortEntries()
        writeAll()
    }
}