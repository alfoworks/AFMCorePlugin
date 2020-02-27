package ru.allformine.afmcp.dataitem;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;

public class DataItem {
    public DataItem() {

    }

    public static DataItem fromItemStack(ItemStack stack) {
        return new DataItem();
    }

    public ItemStack toItemStack() {
        return ItemStack.of(ItemTypes.ITEM_FRAME);
    }

    public void set(String key, Object value) {

    }

    public Object get(String key) {
        return new Object();
    }
}
