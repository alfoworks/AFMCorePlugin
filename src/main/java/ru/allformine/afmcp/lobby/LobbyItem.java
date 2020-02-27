package ru.allformine.afmcp.lobby;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import ru.allformine.afmcp.dataitem.DataItem;

import java.util.UUID;

public class LobbyItem {
    public Text name;
    public ItemType itemType;
    public int id;
    public int slutIndex;
    private LobbyItemClick click;
    private short damage = 0;
    private UUID skullUuid;

    public LobbyItem(String name, TextColor color, ItemType itemType, int id, LobbyItemClick click) {
        this.name = Text.builder().append(Text.of(name)).color(color).build();
        this.itemType = itemType;
        this.id = id;
        this.click = click;
    }

    public LobbyItem(String name, TextColor color, ItemType itemType, int id, short damage, LobbyItemClick click) {
        this(name, color, itemType, id, click);
        this.damage = damage;
    }

    public LobbyItem(String name, TextColor color, ItemType itemType, int id, UUID skullUuid, LobbyItemClick click) {
        this(name, color, itemType, id, click);
        this.skullUuid = skullUuid;
    }

    public void onClick(Player player) {
        click.onClick(player);
    }

    public ItemStack getAsItemStack() {
        ItemStack stack = ItemStack.of(itemType);
        DataItem dataItem = DataItem.fromItemStack(stack);

        dataItem.set("afmcp_lobby_item_id", id);

        return dataItem.toItemStack();
    }

    public interface LobbyItemClick {
        void onClick(Player player);
    }
}
