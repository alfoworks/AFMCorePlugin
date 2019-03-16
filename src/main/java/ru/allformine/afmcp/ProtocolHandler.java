package ru.allformine.afmcp;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.kitteh.vanish.VanishManager;

import java.util.*;

public class ProtocolHandler {
    static VanishManager vanishManager;
    private static Random random = new Random();
    private static Plugin plugin = AFMCorePlugin.getPlugin();

    private static ChatColor getRandomColor() {
        return References.colors[random.nextInt(References.colors.length)];
    }

    static void startHandler() {
        if (plugin.getConfig().getBoolean("protocol.hide_vanished.serverListPing")) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(plugin, ListenerPriority.NORMAL, Collections.singletonList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    List<WrappedGameProfile> players = new ArrayList<>();
                    for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if (!isPlayerVanished(p.getName())) {
                            players.add(new WrappedGameProfile(UUID.randomUUID(), p.getDisplayName()));
                        }
                    }
                    ping.setPlayersOnline(players.size());
                    ping.setPlayers(players);

                    ping.setMotD(getNewMoTD());

                    if (plugin.getConfig().getBoolean("server_maintenance.enabled")) {
                        ping.setVersionName("Сервер на тех. работах!");
                    }
                }
            });
        }
    }

    public static boolean isPlayerVanished(String nickname) { // Метод публичный, потому что будет использоваться в модах.
        if (vanishManager == null) {
            return false;
        }

        return vanishManager.isVanished(nickname);
    }

    //----------------------------------------------------------------------------//

    private static String getNewMoTD() {
        String MoTD;
        String firstLine = ChatColor.AQUA + plugin.getConfig().getString("protocol.motd.firstLine");
        String secondLine = ChatColor.RED + plugin.getConfig().getString("protocol.motd.secondLine");

        MoTD = StringUtils.center(firstLine, 42) + "\n" + StringUtils.center(secondLine, 42);

        return MoTD;
    }
}
