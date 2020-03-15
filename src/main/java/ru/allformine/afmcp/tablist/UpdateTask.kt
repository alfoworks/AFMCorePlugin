package ru.allformine.afmcp.tablist

import org.spongepowered.api.Sponge

class UpdateTask: Runnable {
    override fun run() {
        WrappedTabList.clearEntries()
        for(player in Sponge.getServer().onlinePlayers) {
            WrappedTabList.addEntry(player)
        }
        WrappedTabList.sortEntries()
        WrappedTabList.writeAll()
    }

}