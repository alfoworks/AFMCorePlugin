package ru.alfomine.afmcp.customitem;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class CustomItemManager {
    public static Set<CustomItem> items = new HashSet<>();

    public static void addCustomItem(CustomItem item) {
        items.add(item);
    }

    public static ItemStack createItemById(String id) {
        for (CustomItem item : items) {
            if (item.getId().equals(id)) {
                ItemStack stack = new ItemStack(item.getMaterial(), 1);

                net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

                NBTTagCompound tag = new NBTTagCompound();
                tag.setString("afmcm_ci_id", item.getId());
                nmsStack.setTag(tag);

                stack = CraftItemStack.asBukkitCopy(nmsStack);

                ItemMeta m = stack.getItemMeta();
                m.setDisplayName(ChatColor.AQUA + item.getName());
                stack.setItemMeta(m);

                return stack;
            }
        }

        return null;
    }
}
