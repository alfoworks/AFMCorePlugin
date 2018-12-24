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
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.allformine.afmcp.CFNTasks.CFNTaskSpace;
import ru.allformine.afmcp.CFNTasks.CFNTaskTechno;
import ru.allformine.afmcp.net.discord.Discord;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.VanishManager;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import java.util.*;
import static ru.allformine.afmcp.References.frozenPlayers;

public class AFMCorePlugin extends JavaPlugin implements Listener {
    private VanishManager vmng = null;

    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        new EventListener(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");

        //костыль для определения сервера - спейс или технач, для запуска нужной задачи в планировщике
        if(manager.getPlugin("Factions") != null) { //можно было бы сделать с помощью конфига, но мне чот лень этим заниматсо
            new CFNTaskTechno(this).runTaskTimer(this, 60, 20);
        } else {
            new CFNTaskSpace(this).runTaskTimer(this, 60, 20);
        }

        try { //Проверялка на то, есть ли плагин на ваниш.
            //noinspection deprecation
            vmng = VanishNoPacket.getManager();
        } catch(VanishNotLoadedException ex) {
            System.out.println("Can't found VanishNoPacket.");
        }

        if (vmng != null) { //Анти-ванишепалилка
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
                    MOTD = MOTD+"\n"+StringUtils.center(ChatColor.YELLOW+"Закрытый бета-тест", 40);

                    ping.setMotD(MOTD);
                }
            });
        }

        Discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1, this); //отправляем в дс сообщеньку, что сервак врублен.
    }

    //Сообщение в дискорд о том, что сервер упал.
    public void onDisable() {
        Discord.sendMessageSync("Сервер упал!", false, "TechInfo", 1);
    }

    //Ебанные команды
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("afmcp")) {
            sender.sendMessage("Твоя мать шлюха");
            return true;
        } else if(cmd.getName().equalsIgnoreCase("freeze")) {
            if(args.length > 0) {
                @SuppressWarnings("deprecation")
                Player player = Bukkit.getPlayer(args[0]); //да мне похуй, что оно блядь не поддерживается. МНЕ ПОХУЙ!

                if(player != null) {
                    if(!frozenPlayers.contains(player)) {
                        frozenPlayers.add(player);

                        sender.sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вы успешно заморозили этого игрока.");
                        player.sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вас заморозили.");
                    } else {
                        frozenPlayers.remove(player);

                        sender.sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вы успешно разморозили этого игрока.");
                        player.sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вас разморозили.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> игрок не найден.");
                }
            } else {
                return false;
            }
            return true;
        } else if(cmd.getName().equalsIgnoreCase("rawbc")) {
            if(args.length > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));

                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
