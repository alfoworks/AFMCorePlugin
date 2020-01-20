package ru.alfomine.afmcp.tablist;

import com.comphenix.packetwrapper.WrapperPlayServerPlayerListHeaderFooter;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
        this.name = String.format("%s%s", ChatColor.translateAlternateColorCodes('&', this.permissionUser.getPrefix()), this.permissionUser.getName());
        EntityPlayer nmsPlayer = ((CraftPlayer) player).getHandle();

        this.latency = nmsPlayer.ping;
        this.uuid = player.getUniqueId();
        this.gameMode = player.getGameMode();

        this.header = "Хедер строка 1\nстрока2\nстрока3";
        this.footer = "Футер строка 1\nстрока2\nстрока3";
    }
    
    public PlayerInfoData getAsPlayerInfoData() {
        return new PlayerInfoData(
                new WrappedGameProfile(this.uuid, this.permissionUser.getName()), this.latency,
                EnumWrappers.NativeGameMode.fromBukkit(this.gameMode), WrappedChatComponent.fromText(this.name));
    }

    public void sendHeaderFooterPacket() {
        WrapperPlayServerPlayerListHeaderFooter packetHeaderFooter = new WrapperPlayServerPlayerListHeaderFooter();
        packetHeaderFooter.setFooter(WrappedChatComponent.fromText(this.footer));
        packetHeaderFooter.setHeader(WrappedChatComponent.fromText(this.header));

        packetHeaderFooter.sendPacket(Bukkit.getPlayer(this.uuid));
    }

    @Override
    public boolean equals(Object b) {
        if (b instanceof WrappedTabListEntry) {
            return this.uuid == ((WrappedTabListEntry) b).uuid;
        } else {
            return false;
        }
    }
}
