package ru.allformine.afmcp.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import ru.allformine.afmcp.AFMCorePlugin;

public class TestEventListener {
    @Listener(beforeModifications = true, order = Order.FIRST)
    public void onBlockEvent(ChangeBlockEvent.Break event) {
        if (!AFMCorePlugin.debugSwitch) return;

        System.out.println("Cancel");
        event.setCancelled(true);
    }
}
