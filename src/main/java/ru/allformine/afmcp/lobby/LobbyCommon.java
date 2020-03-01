package ru.allformine.afmcp.lobby;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.entity.DamageEntityEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PluginConfig;
import ru.allformine.afmcp.dataitem.DataItem;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class LobbyCommon {
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
        if (!PluginConfig.lobbyEnabled) {
            return false;
        }

        if (lobbyPlayers.contains(player)) {
            return false;
        }

        if (PluginConfig.lobbySpawn == null) {
            AFMCorePlugin.logger.error("Lobby spawn location is not set. Lobby is not working.");

            return false;
        }

        previousStates.put(player, new PlayerState(player));
        lobbyPlayers.add(player);

        player.setLocation(PluginConfig.lobbySpawn);

        player.sendMessage(Text.of("Добро пожаловать в лобби!"));

        if (player.hasPermission("afmcp.lobby.exit")) {
            player.sendMessage(Text.of("Пропишите /lobby exit, чтобы выйти из лобби."));
        }

        // На всякий случай очищаем инвентарь, мало ли, говно какое-нибудь будет

        player.getInventory().clear();

        // Хп/жрачка/опыт на дефолт
        player.health().set(20D);
        player.foodLevel().set(20);
        player.offer(Keys.TOTAL_EXPERIENCE, 0);

        player.gameMode().set(GameModes.ADVENTURE);

        // Добавляем кнопки
        LobbyPlayerInventory inventory = new LobbyPlayerInventory(player);

        inventory.addItem(new LobbyItem("Выбрать королевство", TextColors.AQUA, ItemTypes.NETHER_STAR, 0, (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 0);

        inventory.addItem(new LobbyItem("Эффекты", TextColors.GOLD, ItemTypes.BLAZE_POWDER, 1, (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 1);

        inventory.addItem(new LobbyItem("Магазин", TextColors.GREEN, ItemTypes.EMERALD, 2, (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 2);

        inventory.addItem(new LobbyItem("Мой профиль", TextColors.BLUE, ItemTypes.SKULL, 3, player.getUniqueId(), (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 4);

        inventory.addItem(new LobbyItem("Видимость игроков: включена", TextColors.GREEN, ItemTypes.DYE, 4, (short) 10, (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 7);

        inventory.addItem(new LobbyItem("Информация", TextColors.DARK_AQUA, ItemTypes.BOOK, 4, (Player clickPlayer) -> {
            clickPlayer.sendMessage(Text.of("Beu beu"));
        }), 8);

        lobbyPlayerInventories.put(player, inventory);

        return true;
    }

    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event){
        if (event.getTargetEntity().hasPermission("afmcp.lobby.ignore")) {
            return; // Игрок уже зарегистрирован в каком-то из городов или у него стоит игнор лобби
        }

        addPlayerToLobby(event.getTargetEntity());
    }

    @Listener
    public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
        if (lobbyPlayers.remove(event.getTargetEntity())) lobbyPlayerInventories.remove(event.getTargetEntity());
        previousStates.remove(event.getTargetEntity());
    }
    // ======== Классы для взаимодействия в лобби ======== //

    @Listener
    public void onInteract(InteractEvent event, @Root ItemStack item) {
        if (!(event.getSource() instanceof Player)) {
            return;
        }

        DataItem dataItem = DataItem.fromItemStack(item);
        Player player = (Player) event.getSource();

        if (dataItem.get("afmcp_lobby_item_id") == null) {
            return;
        }

        int id = (Integer) dataItem.get("afmcp_lobby_item_id");

        for (LobbyItem lobbyItem : lobbyPlayerInventories.get(player).items) {
            if (lobbyItem.id == id) {
                lobbyItem.onClick(player);
                return;
            }
        }
    }

    @Listener
    public void onInventoryChange(ChangeInventoryEvent event) {
        event.setCancelled(lobbyPlayers.contains(event.getSource()));
    }

    @Listener
    public void onBlockPlace(ChangeBlockEvent.Break event, @Root Player player) {
        event.setCancelled(lobbyPlayers.contains(player));
    }

    @Listener
    public void onBlockBreak(ChangeBlockEvent.Place event, @Root Player player) {
        event.setCancelled(lobbyPlayers.contains(player));
    }

    @Listener
    public void onDamage(DamageEntityEvent event) {
        event.setCancelled(event.getTargetEntity() instanceof Player && lobbyPlayers.contains(event.getTargetEntity()));
    }
}
