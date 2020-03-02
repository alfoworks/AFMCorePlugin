package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.scheduler.Task;
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

            inventory.addItem(new LobbyItem("Выбрать королевство", TextColors.AQUA, ItemTypes.NETHER_STAR, 0, (Player clickPlayer) -> {
                clickPlayer.sendMessage(Text.of("Beu beu"));
            }), 0);

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
