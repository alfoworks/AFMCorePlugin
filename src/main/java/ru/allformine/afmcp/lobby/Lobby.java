package ru.allformine.afmcp.lobby;

import com.sun.media.jfxmedia.events.PlayerStateEvent;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameMode;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import ru.allformine.afmcp.AFMCorePlugin;

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

        //noinspection ConstantConditions
        if(true){//if (PluginConfig.lobbySpawnLocation.equals("")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.DARK_RED + "Алярм! Позиция спавна лобби не установлена. Лобби не работает!!!");

            return false;
        }

        previousStates.put(player, new PlayerStateEvent.PlayerState(player));

        // player.setLocation(Локацию сюда);

        lobbyPlayers.add(player);
        player.sendMessage(Text.of("Добро пожаловать в лобби!"));

        if (player.hasPermission("afmcp.lobby.exit")) {
            player.sendMessage(Text.of("Пропишите /lobby exit, чтобы выйти из лобби."));
        }

        // На всякий случай очищаем инвентарь, мало ли, говно какое-нибудь будет

        player.getInventory().clear();

        // Хп/уровень/~~опыт~~ на дефолт TODO ОПыт
        player.health().set(20D);
        player.foodLevel().set(20);

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

        player.gameMode().set(GameMode.ADVENTURE);

        return true;
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event){
        if (AFMCorePlugin.config.get("lobby.playerCities." + event.getTargetEntity().getName()) != null
                || event.getTargetEntity().hasPermission("afmcp.lobby.ignore")) {
            return; // Игрок уже зарегистрирован в каком-то из городов или у него стоит игнор лобби
        }

        addPlayerToLobby(event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event){
        if (lobbyPlayers.remove(event.getTargetEntity())) lobbyPlayerInventories.remove(event.getTargetEntity());
        previousStates.remove(event.getTargetEntity());
    }
    // ======== Классы для взаимодействия в лобби ======== //

    @Listener // TODO
    public void onInteract(InteractEvent event){
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

    @EventHandler // TODO
    public void onSpamwHandItems(PlayerSwapHandItemsEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler // TODO
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler // TODO
    public void onBlockBreal(BlockBreakEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler // TODO
    public void onDamage(EntityDamageEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && lobbyPlayers.contains((Player) event.getEntity()));
    }

    // Пиздец сколько ивентов (это все запрет на изменение инвентаря, чтобы кнопки оставались на своих местах)

    @EventHandler // TODO
    public void onInventoryDrag(InventoryDragEvent event) {
        event.setCancelled(event.getWhoClicked() instanceof Player && lobbyPlayers.contains((Player) event.getWhoClicked()));
    }

    @EventHandler // TODO
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(event.getWhoClicked() instanceof Player && lobbyPlayers.contains((Player) event.getWhoClicked()));
    }

    @EventHandler // TODO
    public void onInventoryPickupItem(EntityPickupItemEvent event) {
        event.setCancelled(event.getEntity() instanceof Player && lobbyPlayers.contains((Player) event.getEntity()));
    }

    @EventHandler // TODO
    public void onItemConsume(PlayerItemConsumeEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler // TODO
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getPlayer()));
    }

    @EventHandler // TODO
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(lobbyPlayers.contains((Player) event.getEntity()));
    }
}
