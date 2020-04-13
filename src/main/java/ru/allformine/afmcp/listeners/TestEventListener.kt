package ru.allformine.afmcp.listeners

import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.block.ChangeBlockEvent
import ru.allformine.afmcp.AFMCorePlugin

class TestEventListener {
    @Listener(beforeModifications = true, order = Order.FIRST)
    fun onBlockEvent(event: ChangeBlockEvent.Break) {
        if (!AFMCorePlugin.debugSwitch) return

        println("Cancel")
        event.isCancelled = true
    }
}