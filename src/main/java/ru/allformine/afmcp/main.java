package ru.allformine.afmcp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class main extends JavaPlugin implements Listener {
    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(this,this);

        ProtocolLibrary.getProtocolManager().addPacketListener(
            new PacketAdapter(this, ListenerPriority.NORMAL,
                Arrays.asList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {

                    @Override
                    public void onPacketSending(PacketEvent event) {
                        WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                        //блять, да пусть жалуется, что deprecated, мне-то что???
                        ping.setPlayers(Arrays.asList(new WrappedGameProfile("00000000-0000-0000-0000-000000000000",
                                "AllForMine")));
                    }
                });
    }
}
