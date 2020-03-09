package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.PluginConfig;

import java.util.concurrent.TimeUnit;

public class LobbyVanilla extends LobbyCommon {
    @Override
    public String getLobbyId() {
        return "vanilla";
    }

    @Override
    public boolean addPlayerToLobby(Player player) {
        if (super.addPlayerToLobby(player)) {
            LobbyPlayerInventory inventory = new LobbyPlayerInventory(player);

            inventory.addItem(new LobbyItem("Выход из лобби", TextColors.AQUA, ItemTypes.COMPASS, 0, (Player clickPlayer) -> {
                sendLobbyMessage(clickPlayer, "Beubass!");
            }), 0);

            inventory.addItem(new LobbyItem("Информация", TextColors.YELLOW, ItemTypes.BOOK, 1, (Player clickPlayer) -> {
                BookView bookView = BookView.builder()
                        .title(Text.of("Информация"))
                        .author(Text.of("Твоя мать"))
                        .addPage(Text.of("Добро пожаловать на ALFO:MINE Vanilla!"))
                        .build();

                clickPlayer.sendBookView(bookView);
            }), 8);

            lobbyPlayerInventories.put(player, inventory);

            return true;
        } else {
            return false;
        }
    }

    @Listener
    public void onPlayerMove(MoveEntityEvent event) {
        if (event.getTargetEntity() instanceof Player && event.getTargetEntity().getLocation().getY() < -50) {
            Task.builder().delay(1, TimeUnit.SECONDS).execute(() -> event.getTargetEntity().setLocation(PluginConfig.lobbySpawn)).submit(AFMCorePlugin.instance);
        }
    }
}
