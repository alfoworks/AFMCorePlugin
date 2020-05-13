package ru.allformine.afmcp.lobby;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColor;
import ru.allformine.afmcp.dataitem.DataItem;

public class LobbyItem {
	
	public Text name;
	public ItemType itemType;
	public int id;
	public int slutIndex;
	private LobbyItemClick click;
	private DyeColor dyeColor;
	private GameProfile profile;
	
	public LobbyItem(String name, TextColor color, ItemType itemType, int id, LobbyItemClick click) {
		this.name = Text.builder().append(Text.of(name)).color(color).build();
		this.itemType = itemType;
		this.id = id;
		this.click = click;
	}
	
	public LobbyItem(String name, TextColor color, int id, GameProfile profile, LobbyItemClick click) {
		this(name, color, ItemTypes.SKULL, id, click);
		this.profile = profile;
	}
	
	public LobbyItem(String name, TextColor color, ItemType itemType, int id, DyeColor dyeColor, LobbyItemClick click) {
		this(name, color, itemType, id, click);
		this.dyeColor = dyeColor;
	}
	
	public void onClick(Player player) {
		click.onClick(player);
	}
	
	public ItemStack getAsItemStack() {
		ItemStack stack = /* ItemStack.builder().fromContainer(ItemStack.of(itemType).toContainer().set(DataQuery.of("UnsafeDamage"), damage)).build(); */ ItemStack.of(itemType);
		stack.offer(Keys.DISPLAY_NAME, name);
		
		if (dyeColor != null) {
			stack.offer(Keys.DYE_COLOR, dyeColor);
		}
		
		if (profile != null) {
			stack.offer(Keys.REPRESENTED_PLAYER, profile);
		}
		
		DataItem dataItem = new DataItem(stack);
		
		dataItem.set("afmcp_lobby_item_id", id);
		
		return dataItem.toItemStack();
	}
	
	public interface LobbyItemClick {
		
		void onClick(Player player);
	}
}
