package ru.allformine.afmcp.lobby;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.UUID;

public class LobbyItem {
    public String name;
    public Material material; // TODO Что это блять??
    public int id;
    public int slutIndex;
    private LobbyItemClick click;
    private short damage = 0;
    private UUID skullUuid;

    public LobbyItem(String name, Material material, int id, LobbyItemClick click) {
        this.name = name;
        this.material = material;
        this.id = id;
        this.click = click;
    }

    public LobbyItem(String name, Material material, int id, short damage, LobbyItemClick click) {
        this(name, material, id, click);
        this.damage = damage;
    }

    public LobbyItem(String name, Material material, int id, UUID skullUuid, LobbyItemClick click) {
        this(name, material, id, click);
        this.skullUuid = skullUuid;
    }

    public void onClick(Player player) {
        click.onClick(player);
    }

    public ItemStack getAsItemStack() {
        ItemStack stack = new ItemStack(this.material);

        net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        NBTTagCompound tag = new NBTTagCompound();
        tag.setInt("afmcp_lobby_id", this.id);
        nmsStack.setTag(tag);

        stack = CraftItemStack.asBukkitCopy(nmsStack);

        if (skullUuid != null) {
            SkullMeta meta = (SkullMeta) stack.getItemMeta();

            meta.setOwningPlayer(Bukkit.getOfflinePlayer(skullUuid));
            meta.setDisplayName(ChatColor.RESET + this.name);

            stack.setItemMeta(meta);
            stack.setDurability((short) 3);
        } else {
            ItemMeta m = stack.getItemMeta();
            m.setDisplayName(ChatColor.RESET + this.name);

            stack.setItemMeta(m);
            stack.setDurability(this.damage);
        }

        return stack;
    }

    public interface LobbyItemClick {
        void onClick(Player player);
    }
}
