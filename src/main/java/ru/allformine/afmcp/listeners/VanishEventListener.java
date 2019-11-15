package ru.allformine.afmcp.listeners;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.weather.Lightning;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.message.MessageChannelEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.event.server.ClientPingServerEvent;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import ru.allformine.afmcp.vanish.VanishEffects;
import ru.allformine.afmcp.vanish.VanishManager;

public class VanishEventListener {
    @Listener(order = Order.PRE)
    public void onPlayerLogin(ClientConnectionEvent.Login event) {
        VanishManager.playersToRemove.add(event.getProfile().getName().get());
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.tabList.addTabListPlayer(event.getTargetEntity().getName());

            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.vanishPlayer(event.getTargetEntity(), true);
        }

        VanishManager.playersToRemove.remove(event.getTargetEntity().getName());

        event.setMessageCancelled(true);
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        if (!event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            return;
        }

        if (event.getTargetEntity().hasPermission(VanishManager.vanishPermission)) {
            VanishManager.unvanishPlayer(event.getTargetEntity(), true);
        }

        event.setMessageCancelled(true);
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

    @Listener
    public void onClickInventory(ClickInventoryEvent event, @Root Player player) {
        if (!VanishManager.isVanished(player)) return;

        if (event instanceof ClickInventoryEvent.NumberPress) return;
        if (event instanceof ClickInventoryEvent.Middle) return;
        if (event.getTargetInventory().getArchetype() == InventoryArchetypes.PLAYER) return;
        event.setCancelled(true);
    }

    // ========================================== //

    @Listener
    public void onClientPingServerEvent(ClientPingServerEvent event) {
        if (!event.getResponse().getPlayers().isPresent()) return;

        ClientPingServerEvent.Response.Players players = event.getResponse().getPlayers().get();
        players.setOnline(VanishManager.getPlayerCountExcludingVanished());

        players.getProfiles().removeIf(gameProfile -> VanishManager.isVanished(Sponge.getServer().getPlayer(gameProfile.getUniqueId()).get()));
    }

    // =Отмена дамага от молнии (эффект переключения ваниша)= //

    @Listener
    public void onEntityDamage(DamageEntityEvent event) {
        for (Entity entity : event.getCause().allOf(Entity.class)) {
            if (entity instanceof Lightning && VanishEffects.lightnings.contains(entity)) event.setCancelled(true);
        }
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent event) {
        for (Entity entity : event.getCause().allOf(Entity.class)) {
            if (entity instanceof Lightning && VanishEffects.lightnings.contains(entity)) event.setCancelled(true);
        }
    }


    // ====================================================== //
}
