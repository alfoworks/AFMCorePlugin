package ru.allformine.afmcp.dataitem;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;

public class DataItem {
	
	ItemStack stack;
	
	public DataItem(ItemStack stack) {
		this.stack = stack;
	}
	
	public ItemStack toItemStack() {
		return stack;
	}
	
	public void set(String key, Object value) {
		DataContainer container = stack.toContainer();
		
		container.set(DataQuery.of("UnsafeData", key), value);
		
		stack = ItemStack.builder().fromContainer(container).build();
	}
	
	public Optional<Object> get(String key) {
		DataContainer container = stack.toContainer();
		
		return container.get(DataQuery.of("UnsafeData", key));
	}
}
