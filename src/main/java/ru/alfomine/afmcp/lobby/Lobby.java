package ru.alfomine.afmcp.lobby;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.HashSet;
import java.util.Set;

public class Lobby implements Listener {
    Set<Player> lobbyPlayers = new HashSet<>();

    public String getName() {
        return "";
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (AFMCorePlugin.config.get("lobby.playerCities." + event.getPlayer().getName()) != null || event.getPlayer().hasPermission("afmcp.lobby.ignore")) {
            return; // Игрок уже зарегистрирован в каком-то из городов или у него стоит игнор лобби
        }

        if (PluginConfig.lobbySpawnLocation.equals("")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Алярм! Позиция спавна лобби не установлена. Лобби не работает!!!");

            return;
        }

        lobbyPlayers.add(event.getPlayer());
        sendStyledMessage(event.getPlayer(), "Добро пожаловать в лобби!");

        //На всякий случай очищаем инвентарь, мало ли, говно какое-нибудь будет

        event.getPlayer().getInventory().clear();

        // Добавляем кнопки

        event.getPlayer().getInventory().setItem(0, new ItemStack(new LobbyItem("Test lobby item", Material.DIAMOND_PICKAXE, 1, (Player player) -> {
            player.sendMessage("Beubass!");
        }).material));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        lobbyPlayers.remove(event.getPlayer());
    }

    // ======== Классы для взаимодействия в лобби ======== //

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler
    public void onBlockBreal(BlockBreakEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && lobbyPlayers.contains((Player) event.getEntity()));
    }

    // Пиздец сколько ивентов (это все запрет на изменение инвентаря, чтобы кнопки оставались на своих местах)

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(event.getWhoClicked() instanceof Player && lobbyPlayers.contains((Player) event.getWhoClicked()));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(event.getWhoClicked() instanceof Player && lobbyPlayers.contains((Player) event.getWhoClicked()));
    }

    @EventHandler
    public void onInventoryPickupItem(EntityPickupItemEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && lobbyPlayers.contains((Player) event.getEntity()));
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    // ========================== //

    private void sendStyledMessage(CommandSender sender, String message) {
        sender.sendMessage(String.format("%sLobby %s> %s", ChatColor.DARK_GREEN, ChatColor.WHITE, message));
    }
}
