package ru.allformine.afmcp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import com.dthielke.herochat.ChannelChatEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import ru.allformine.afmcp.net.discord.discord;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import java.util.*;

public class main extends JavaPlugin implements Listener {
    private VanishManager vmng = null;

    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(this,this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");

        BukkitTask CFN = new CFNChannelTask(this,
                manager.getPlugin("Factions")).runTaskTimer(this,
                60,
                20);

        try {
            vmng = VanishNoPacket.getManager();
        } catch(VanishNotLoadedException ex) {
            System.out.println("Can't found VanishNoPacket.");
        }

        if (vmng != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    List < WrappedGameProfile > players = new ArrayList < WrappedGameProfile > ();

                    ping.setPlayersOnline(200);
                    ping.setPlayersMaximum(400);
                    ping.setMotD("Test MOTD.");
                    ping.setPlayers(players);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean isStaff = event.getPlayer().hasPermission("afmcp.staff");
        String message = "(SpaceUnion) Игрок **вошел** в меня, о даа.";

        if(isStaff) {
            message += " (персонал)";

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+event.getPlayer().getName()+" "+ChatColor.GREEN+"вошел в игру! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }

        discord.sendMessage(message, null, event.getPlayer().getDisplayName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        boolean isStaff = event.getPlayer().hasPermission("afmcp.staff");
        String message = "(SpaceUnion) Игрок **вышел** из игры.";

        if(isStaff) {
            message += " (персонал)";

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+event.getPlayer().getName()+" "+ChatColor.GREEN+"вышел из игры! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }

        discord.sendMessage(message, null, event.getPlayer().getDisplayName());
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void ChannelChatEvent(ChannelChatEvent event) {
        String message = "(SpaceUnion) [**"+event.getChannel().getName()+"**] "+event.getMessage();

        discord.sendMessage(message, null, event.getSender().getName());
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerAchievmentAwarded(PlayerAchievementAwardedEvent event) {
        String message = "(SpaceUnion) Игрок получил достижение **"+event.getAchievement().name()+"**.";

        discord.sendMessage(message, null, event.getPlayer().getDisplayName());
    }

    public void onDisable() {
        System.out.println("Disabling AFMCP.");
    }
}
