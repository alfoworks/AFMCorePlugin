package ru.allformine.afmcp.listeners;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.world.ExplosionEvent;

public class TestEventListener {
    @Listener(order = Order.FIRST, beforeModifications = true)
    public void onExplosion(ExplosionEvent.Pre event) {
        System.out.println("Event cancelled");
        event.setCancelled(true);
    }
}
