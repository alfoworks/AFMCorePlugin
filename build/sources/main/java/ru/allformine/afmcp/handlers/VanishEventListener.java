package ru.allformine.afmcp.handlers;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishEventListener {
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.vanishPlayer(event.getTargetEntity(), true);
        }

        event.clearMessage();
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.unvanishPlayer(event.getTargetEntity(), true);
        }

        event.clearMessage();
    }

    // ========================================== //

    @Listener
    public void onInteract(InteractEvent event, @Root Player player) {
        if (!VanishManager.isVanished(player)) return;

        event.setCancelled(true);
    }

    @Listener
    public void onPlayerChat(MessageChannelEvent.Chat event, @First Player player) {
        if (!VanishManager.isVanished(player)) return;

        event.setCancelled(true);
    }

    @Listener
    public void onPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
        if (!VanishManager.isVanished(player)) return;

        event.setCancelled(true);
    }

    // ========================================== //
}
