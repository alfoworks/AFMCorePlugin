package ru.allformine.afmcp.quests;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import ru.allformine.afmcp.AFMCorePlugin;

public class QuestGUI {

    public void showToPlayer(Player player) {
        Inventory inventory = Inventory.builder().build(Sponge.getPluginManager().getPlugin("afmcp"));
        inventory.slots().forEach(x -> x.set(ItemStack.builder().itemType(ItemTypes.BOOK).build()));
        player.openInventory(inventory);
    }
}
