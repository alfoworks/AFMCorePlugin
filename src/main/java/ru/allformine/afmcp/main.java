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
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ru.allformine.afmcp.net.discord.discord;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import java.util.*;

public class main extends JavaPlugin implements Listener {
    private VanishManager vmng = null;
    private String[] notLoggedCommands = {"/g", "/t", "/l"};
    private String[] triggerWords = {"дюп", "баг", "краш", "нойра"};

    public void onEnable() {
        discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1);

        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(this,this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");

        new CFNChannelTask(this,
                manager.getPlugin("Factions")).runTaskTimer(this,
                60,
                20);

        try {
            //noinspection deprecation
            vmng = VanishNoPacket.getManager();
        } catch(VanishNotLoadedException ex) {
            System.out.println("Can't found VanishNoPacket.");
        }

        if (vmng != null) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Collections.singletonList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    List < WrappedGameProfile > players = new ArrayList<>();
                    for (Player p: Bukkit.getServer().getOnlinePlayers()) {
                        if (!vmng.isVanished(p)) {
                            players.add(new WrappedGameProfile(UUID.randomUUID(), p.getDisplayName()));
                        }
                    }
                    ping.setPlayersOnline(players.size());
                    ping.setPlayers(players);

                    String MOTD;
                    MOTD = StringUtils.center(ChatColor.GOLD+"AllForMine SpaceUnion", 40);
                    MOTD = MOTD+StringUtils.center(ChatColor.YELLOW+"Закрытый бета-тест", 40);

                    ping.setMotD(MOTD);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean isStaff = event.getPlayer().hasPermission("afmcp.staff");
        String message = "Игрок **вошел** в меня, о даа.";

        if(isStaff) {
            message += " (персонал)";

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+event.getPlayer().getName()+" "+ChatColor.GREEN+"вошел в игру! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }

        discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {
        boolean isStaff = event.getPlayer().hasPermission("afmcp.staff");
        String message = "Игрок **вышел** из игры.";

        if(isStaff) {
            message += " (персонал)";

            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(event.getPlayer())) {
                    p.sendMessage(ChatColor.DARK_AQUA+""+event.getPlayer().getName()+" "+ChatColor.GREEN+"вышел из игры! "+ChatColor.DARK_AQUA+"(персонал)");
                }
            }
        }

        discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1);
    }


    @EventHandler(priority = EventPriority.NORMAL)
    public void ChannelChatEvent(ChannelChatEvent event) {
        String message = "[**"+event.getChannel().getName()+"**] "+event.getMessage();
        int logLevel = 1;

        if(event.getChannel().getName().equals("Local")) {
            Location location = event.getSender().getPlayer().getLocation();
            message = "{"+String.valueOf(location.getX())+String.valueOf(location.getY())+String.valueOf(location.getZ())+"} "+message;

            logLevel = 2;
        } else if(event.getChannel().getName().contains("convo")) {
            logLevel = 2;
        }

        for (String item : triggerWords) {
            if(event.getMessage().toLowerCase().contains(item)) {
                message = "Обнаруженно триггер-слово: "+item+" @here\n"+message;
            }
        }

        discord.sendMessage(message, true, event.getSender().getName(), logLevel);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerAchievmentAwarded(PlayerAchievementAwardedEvent event) {
        String message = "Игрок получил достижение **"+event.getAchievement().name()+"**.";

        discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(!event.isCancelled()) { //Возможно это поможет от логирования несуществующих комманд, но я хз чот...
            String[] message = event.getMessage().split(" ");
            String command = message[0];
            if(!Arrays.asList(notLoggedCommands).contains(command)) {
                discord.sendMessage("Игрок выполнил команду **"+event.getMessage()+"**", true, event.getPlayer().getDisplayName(), 2);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerDeath(PlayerDeathEvent event) {
        discord.sendMessage(event.getDeathMessage(), true, event.getEntity().getDisplayName(), 1);
    }

    public void onDisable() {
        discord.sendMessage("Сервер упал!", false, "TechInfo", 1);
    }
}
