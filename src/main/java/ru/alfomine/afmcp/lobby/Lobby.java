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
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;
import ru.alfomine.afmcp.util.LocationUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Lobby implements Listener {
    private Set<Player> lobbyPlayers = new HashSet<>();
    private HashMap<Player, LobbyPlayerInventory> lobbyPlayerInventories = new HashMap<>();
    private HashMap<Player, PlayerState> previousStates = new HashMap<>();

    public boolean removePlayerFromLobby(Player player) {
        if (lobbyPlayers.remove(player)) {
            lobbyPlayerInventories.remove(player);

            player.getInventory().clear();

            if (previousStates.containsKey(player)) {
                previousStates.get(player).applyTo(player);
            }

            return true;
        }

        return false;
    }

    public boolean addPlayerToLobby(Player player) {
        if (lobbyPlayers.contains(player)) {
            return false;
        }

        if (PluginConfig.lobbySpawnLocation.equals("")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Алярм! Позиция спавна лобби не установлена. Лобби не работает!!!");

            return false;
        }

        previousStates.put(player, new PlayerState(player));

        player.teleport(LocationUtil.fromString(PluginConfig.lobbySpawnLocation));

        lobbyPlayers.add(player);
        sendStyledMessage(player, "Добро пожаловать в лобби!");

        if (player.hasPermission("afmcp.lobby.exit")) {
            sendStyledMessage(player, "Пропишите /lobby exit, чтобы выйти из лобби.");
        }

        // На всякий случай очищаем инвентарь, мало ли, говно какое-нибудь будет

        player.getInventory().clear();

        // Хп/уровень/опыт на дефолт
        player.setHealth(20D);
        player.setFoodLevel(20);
        player.setExp(0);

        // Добавляем кнопки
        LobbyPlayerInventory inventory = new LobbyPlayerInventory(player);

        inventory.addItem(new LobbyItem(ChatColor.AQUA + "Выбрать королевство", Material.NETHER_STAR, 0, (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 0);

        inventory.addItem(new LobbyItem(ChatColor.GOLD + "Эффекты", Material.BLAZE_POWDER, 1, (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 1);

        inventory.addItem(new LobbyItem(ChatColor.GREEN + "Магазин", Material.EMERALD, 2, (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 2);

        inventory.addItem(new LobbyItem(ChatColor.BLUE + "Мой профиль", Material.SKULL_ITEM, 3, player.getUniqueId(), (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 4);

        inventory.addItem(new LobbyItem(ChatColor.GREEN + "Видимость игроков: включена", Material.INK_SACK, 4, (short) 10, (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 7);

        inventory.addItem(new LobbyItem(ChatColor.DARK_AQUA + "Информация", Material.BOOK, 4, (Player clickPlayer) -> {
            clickPlayer.sendMessage("Beu beu");
        }), 8);

        lobbyPlayerInventories.put(player, inventory);

        player.setGameMode(GameMode.ADVENTURE);

        return true;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (AFMCorePlugin.config.get("lobby.playerCities." + event.getPlayer().getName()) != null || event.getPlayer().hasPermission("afmcp.lobby.ignore")) {
            return; // Игрок уже зарегистрирован в каком-то из городов или у него стоит игнор лобби
        }

        addPlayerToLobby(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (lobbyPlayers.remove(event.getPlayer())) lobbyPlayerInventories.remove(event.getPlayer());
        previousStates.remove(event.getPlayer());
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

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(lobbyPlayers.contains((Player) event.getEntity()));
    }

    // ========================== //

    private void sendStyledMessage(CommandSender sender, String message) {
        sender.sendMessage(String.format("%sLobby %s> %s", ChatColor.DARK_GREEN, ChatColor.WHITE, message));
    }
}
