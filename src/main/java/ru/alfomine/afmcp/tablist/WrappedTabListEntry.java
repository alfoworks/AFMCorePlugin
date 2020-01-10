package ru.alfomine.afmcp.tablist;

import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.UUID;

public class WrappedTabListEntry {
    public String name;
    public int latency;
    public UUID uuid;
    public GameMode gameMode;
    public PermissionUser permissionUser;

    String header;
    String footer;

    public WrappedTabListEntry(Player player) {
        this.permissionUser = PermissionsEx.getUser(player.getName());
        this.name = String.format("%s %s", this.permissionUser.getPrefix(), this.permissionUser.getName());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        this.latency = nmsPlayer.ping;
        this.uuid = player.getUniqueId();
        this.gameMode = player.getGameMode();

        this.header = "Заглушка 1";
        this.footer = "Заглушка 2";
    }
}
