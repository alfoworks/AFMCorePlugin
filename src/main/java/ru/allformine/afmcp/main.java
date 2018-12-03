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
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class main extends JavaPlugin implements Listener {

    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(this,this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");

        //Бля
        BukkitTask CFN = new CFNChannelTask(this,
                manager.getPlugin("Factions")).runTaskTimer(this,
                60,
                20);

            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);

                    ping.setPlayersOnline(0);
                    ping.setPlayers(new ArrayList<WrappedGameProfile>());
                }
            });


    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("afmcp.staff")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+p.getName()+" "+ChatColor.GREEN+"вошел в игру! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getPlayer().hasPermission("afmcp.staff")) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+p.getName()+" "+ChatColor.GREEN+"вышел из игры! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }
    }

    public void onDisable() {
        System.out.println("Disabling AFMCP.");
    }
}
