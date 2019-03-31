package ru.allformine.afmcp.hadkers;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PluginEvents;

import static ru.allformine.afmcp.References.frozenPlayers;

public class EventListener implements Listener {
    //Сообщение в дискорд о входе/выходе игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PluginEvents.quitOrJoin(event.getPlayer(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PluginEvents.quitOrJoin(event.getPlayer(), false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getDisplayName().equals("Sila_Zemli") || event.getEntity().getDisplayName().equals("Noire")) {
            event.setDeathMessage(event.getEntity().getDisplayName() + " умер от СПИДа");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (AFMCorePlugin.getPlugin().getConfig().getBoolean("server_maintenance.enabled") && !event.getPlayer().hasPermission("afmcp.staff")) {
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', AFMCorePlugin.getPlugin().getConfig().getString("server_maintenance.kickMessage")));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            if (event.getRightClicked().toString().equals("customnpcs-CustomNpc")) {
                System.out.println("Uncancelled onCreatureSpawn event for CustomNPC entity, player " + event.getPlayer().getDisplayName() + ".");
                event.setCancelled(false);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.isCancelled()) {
            if (event.getEntity().toString().equals("customnpcs-CustomNpc")) {
                System.out.println("Uncancelled onCreatureSpawn event for CustomNPC entity.");
                event.setCancelled(false);
            }
        }
    }
}
