package ru.alfomine.afmcp.lobby;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Lobby implements Listener {
    private Set<Player> lobbyPlayers = new HashSet<>();
    private HashMap<Player, LobbyPlayerInventory> lobbyPlayerInventories = new HashMap<>();

    public boolean removePlayerFromLobby(Player player) {
        if (lobbyPlayers.remove(player)) {
            lobbyPlayerInventories.remove(player);

            player.getInventory().clear();

            return true;
        }

        return false;
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

        if (event.getPlayer().hasPermission("afmcp.lobby.exit")) {
            sendStyledMessage(event.getPlayer(), "Пропишите /lobby exit, чтобы выйти из лобби.");
        }

        //На всякий случай очищаем инвентарь, мало ли, говно какое-нибудь будет

        event.getPlayer().getInventory().clear();

        // Добавляем кнопки
        LobbyPlayerInventory inventory = new LobbyPlayerInventory(event.getPlayer());

        inventory.addItem(new LobbyItem(ChatColor.AQUA + "Выбрать королевство", Material.NETHER_STAR, 0, (Player player) -> {
            player.sendMessage("Beu beu");
        }), 0);

        inventory.addItem(new LobbyItem(ChatColor.GOLD + "Эффекты", Material.BLAZE_POWDER, 1, (Player player) -> {
            player.sendMessage("Beu beu");
        }), 1);

        inventory.addItem(new LobbyItem(ChatColor.GREEN + "Магазин", Material.EMERALD, 2, (Player player) -> {
            player.sendMessage("Beu beu");
        }), 2);

        inventory.addItem(new LobbyItem(ChatColor.BLUE + "Мой профиль", Material.SKULL_ITEM, 3, event.getPlayer().getUniqueId(), (Player player) -> {
            player.sendMessage("Beu beu");
        }), 4);

        inventory.addItem(new LobbyItem(ChatColor.GREEN + "Видимость игроков: включена", Material.INK_SACK, 4, (short) 10, (Player player) -> {
            player.sendMessage("Beu beu");
        }), 7);

        inventory.addItem(new LobbyItem(ChatColor.DARK_AQUA + "Информация", Material.BOOK, 4, (Player player) -> {
            player.sendMessage("Beu beu");
        }), 8);

        lobbyPlayerInventories.put(event.getPlayer(), inventory);

        event.getPlayer().setGameMode(GameMode.SURVIVAL);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (lobbyPlayers.remove(event.getPlayer())) lobbyPlayerInventories.remove(event.getPlayer());
    }

    // ======== Классы для взаимодействия в лобби ======== //

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemNms.getTag();

        if (!itemNms.hasTag() || tag == null) {
            return;
        }

        if (!tag.hasKey("afmcp_lobby_id")) {
            return;
        }

        int id = tag.getInt("afmcp_lobby_id");

        for (LobbyItem lobbyItem : lobbyPlayerInventories.get(event.getPlayer()).items) {
            if (lobbyItem.id == id) {
                lobbyItem.onClick(event.getPlayer());
                return;
            }
        }
    }

    @EventHandler
    public void onSpawHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

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
