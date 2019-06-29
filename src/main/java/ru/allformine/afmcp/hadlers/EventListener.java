package ru.allformine.afmcp.hadlers;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PluginEvents;
import ru.allformine.afmcp.References;
import ru.allformine.afmcp.net.api.Webhook;

import java.util.HashMap;
import java.util.Set;

public class EventListener implements Listener {
    private HashMap<Player, ProtectedRegion> playerRegions = new HashMap<>();

    private void updateRegions(Player player) {
        RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());
        ProtectedRegion region = regionManager.getApplicableRegions(player.getLocation()).getRegions().isEmpty() ? null : regionManager.getApplicableRegions(player.getLocation()).getRegions().iterator().next();
        if (region != null) {
            if (playerRegions.get(player) != null && playerRegions.get(player) != region) {
                playerRegions.replace(player, region);

                PluginEvents.onPlayerRegionJoin(player, region);
            } else if (playerRegions.get(player) == null) {
                playerRegions.put(player, region);

                PluginEvents.onPlayerRegionJoin(player, region);
            }
        } else {
            if (playerRegions.get(player) != null) {
                playerRegions.remove(player);

                PluginEvents.onPlayerRegionLeft(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        PluginEvents.quitOrJoin(event.getPlayer(), true);

        Webhook.TypePlayerMessage type = event.getPlayer().hasPlayedBefore() ?
                Webhook.TypePlayerMessage.JOINED_SERVER :
                Webhook.TypePlayerMessage.JOINED_FIRST_TIME;
        Webhook.sendPlayerMessage(type, event.getPlayer());
        updateRegions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PluginEvents.quitOrJoin(event.getPlayer(), false);
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LEFT_SERVER, event.getPlayer());
        if (PluginEvents.playerCurrentMusic.get(event.getPlayer()) != null) {
            PluginEvents.playerCurrentMusic.remove(event.getPlayer());
        }
        if (PluginEvents.playerCurrentNamedRegion.get(event.getPlayer()) != null) {
            PluginEvents.playerCurrentNamedRegion.remove(event.getPlayer());
        }
        if (playerRegions.get(event.getPlayer()) != null) {
            playerRegions.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getDisplayName().equals("Sila_Zemli") || event.getEntity().getDisplayName().equals("Noire")) {
            event.setDeathMessage(event.getEntity().getDisplayName() + " умер от СПИДа (" + event.getDeathMessage() + ")");
        }

        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.DIED,
                event.getEntity().getPlayer(),
                event.getDeathMessage()
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (References.frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }

        updateRegions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (References.frozenPlayers.contains(event.getPlayer())) {
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
                System.out.println("Uncancelled onPlayerInteractEntity event for CustomNPC entity, player " + event.getPlayer().getDisplayName() + ".");
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        updateRegions(event.getPlayer());

        if (References.frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        updateRegions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerPortal(PlayerPortalEvent event) {
        updateRegions(event.getPlayer());

        if (References.frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler
    public void onPlayerAchievement(PlayerAchievementAwardedEvent event) {
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.EARNED_ADVANCEMENT,
                event.getPlayer(),
                event.getAchievement() != null ? event.getAchievement().name() : "НЕИЗВЕСТНО"
        );
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerChat(ChannelChatEvent event) { // TODO: рефакторинг. Полный.
        System.out.println("onPlayerChat");
        String channelName = event.getChannel().getName();
        Player sender = (Player) event.getSender();
        String message = event.getMessage();
        if (channelName.equalsIgnoreCase("Local") || channelName.startsWith("convo") ) {
            if(channelName.startsWith("convo")){
                Set<Chatter> chatMembers = event.getChannel().getMembers();
                for (Chatter chatter : chatMembers) { //Особенно этой херни
                    if (!chatter.getPlayer().getDisplayName().equals(event.getSender().getPlayer().getDisplayName())) {
                        channelName = String.format("%s -> %s",event.getSender().getPlayer().getDisplayName(),chatter.getPlayer().getDisplayName());
                        break;
                    }
                }
            }
            int x = (int) sender.getLocation().getX();
            int y = (int) sender.getLocation().getY();
            int z = (int) sender.getLocation().getZ();
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.LVL2_CHAT_MESSAGE,
                    sender,
                    String.format("X: %s, Y: %s, Z: %s", x, y, z),
                    channelName,
                    message
            );
        } else {
            Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.CHAT_MESSAGE, sender, channelName, message);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Webhook.sendPlayerMessage(Webhook.TypePlayerMessage.COMMAND, event.getPlayer(), event.getMessage());
    }

    @EventHandler
    public void onPlayerItemDrop(PlayerDropItemEvent event) {
        if (References.frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler
    public void onPlayerSlotChange(PlayerItemHeldEvent event) {
        if (References.frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (player != null && References.frozenPlayers.contains(player)) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }
    }
}
