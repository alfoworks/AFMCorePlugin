package ru.alfomine.afmcp.listeners;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.alfomine.afmcp.customitem.CustomItem;
import ru.alfomine.afmcp.customitem.CustomItemManager;

public class CustomItemListener implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();

        if (item == null) {
            return;
        }

        net.minecraft.server.v1_12_R1.ItemStack itemNms = CraftItemStack.asNMSCopy(item);
        NBTTagCompound tag = itemNms.getTag();

        if (!itemNms.hasTag() || tag == null) {
            return;
        }

        if (!tag.hasKey("afmcm_ci_id")) {
            return;
        }

        String tagString = tag.getString("afmcm_ci_id");

        for (CustomItem customItem : CustomItemManager.items) {
            if (customItem.getId().equals(tagString)) {
                customItem.onUse(event.getPlayer());
                event.setCancelled(true);
                break;
            }
        }
    }
}
