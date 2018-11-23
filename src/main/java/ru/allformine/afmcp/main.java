package ru.allformine.afmcp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import java.util.*;

public class main extends JavaPlugin implements Listener {
    VanishManager vmng = null;

    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(this,this);

        try {
            vmng = VanishNoPacket.getManager();
        } catch(VanishNotLoadedException ex) {
            System.out.println("Can't found VanishNoPacket.");
        }

        if(vmng != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                        List<WrappedGameProfile> players = Collections.emptyList();

                        for (Player p: Bukkit.getServer().getOnlinePlayers()) {
                            if(!vmng.isVanished(p)) {
                                players.add(new WrappedGameProfile(UUID.randomUUID(), p.getDisplayName()));
                            }
                        }

                        ping.setPlayersOnline(players.size());
                        ping.setPlayers(players);
                    }
                });
        }
    }

    public void onDisable() {
        System.out.println("Disabling AFMCP.");
    }
}
