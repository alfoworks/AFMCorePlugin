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
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.net.eco.eco;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import ru.allformine.afmcp.net.discord.Discord;
import org.bukkit.plugin.java.JavaPlugin;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import static ru.allformine.afmcp.References.frozenPlayers;

public class AFMCorePlugin extends JavaPlugin implements Listener {
    private JavaPlugin pl = this;

    public void onEnable() {
        new EventListener(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "FactionsShow");

        this.saveDefaultConfig();

        try { //Проверялка на то, есть ли плагин на ваниш.
            //noinspection deprecation
            References.vmng = VanishNoPacket.getManager();
        } catch(VanishNotLoadedException ex) {
            System.out.println("Can't found VanishNoPacket.");
        }

        if (References.vmng != null) { //Анти-ванишепалилка
            ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Collections.singletonList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC) {
                @Override
                public void onPacketSending(PacketEvent event) {
                    WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                    List < WrappedGameProfile > players = new ArrayList<>();
                    for (Player p: Bukkit.getServer().getOnlinePlayers()) {
                        if (!References.vmng.isVanished(p)) {
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

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, Collections.singletonList(PacketType.Handshake.Client.SET_PROTOCOL), ListenerOptions.ASYNC) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Discord.sendMessage(event.getPacket().getStrings().read(0), false, "Debug", 1, pl);
            }
        });

        Discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1, this); //отправляем в дс сообщеньку, что сервак врублен.
    }

    //Сообщение в дискорд о том, что сервер упал.
    public void onDisable() {
        Discord.sendMessageSync("Сервер упал!", false, "TechInfo", 1, this);
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
        } else if(cmd.getName().equalsIgnoreCase("vip")) {
            if(sender instanceof Player) {
                if(args.length > 0 && this.getConfig().contains("vips."+args[0])) {
                    String playerBal = eco.getBalance(sender.getName());
                    int cost = this.getConfig().getInt("vips."+args[0]+".cost");

                    if(playerBal == null) {
                        sender.sendMessage(ChatColor.RED+"AFMEco "+ChatColor.WHITE+"> Произошла ошибка при выполнении команды.");
                        return true;
                    }

                    if(Integer.valueOf(playerBal) < cost) {
                        int needed = cost - Integer.valueOf(playerBal);

                        sender.sendMessage(ChatColor.RED
                                +"AFMEco "
                                +ChatColor.WHITE
                                +"> У вас недостаточно токенов. Вам нужно еще "
                                +ChatColor.RED
                                +String.valueOf(needed)
                                +" токенов"+ChatColor.WHITE
                                +".");
                        return true;
                    }

                    sender.sendMessage(ChatColor.GREEN+"AFMEco "+ChatColor.WHITE+"> Вы успешно приобрели привелегию. Спасибо!");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givevip "+sender.getName()+" "+args[0]+" 30");
                    Discord.sendMessage("@everyone\nИгрок "+sender.getName()+" приобрёл подписку **"+args[0]+"**!!!", false, "DonationAlerts", 1, this);

                    eco.rem(sender.getName(), String.valueOf(cost), this);

                    return true;
                } else {
                    sender.sendMessage(ChatColor.YELLOW+"AFMEco "+ChatColor.WHITE+"> Список доступных привелегий:");

                    for(String key : this.getConfig().getConfigurationSection("vips").getKeys(false)) {
                        sender.sendMessage(ChatColor.YELLOW
                                +key
                                +ChatColor.WHITE
                                +" - "
                                +ChatColor.YELLOW
                                +String.valueOf(this.getConfig().getInt("vips."+key+".cost"))
                                +" токенов");
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED+"Данная команда может быть выполнена только игроком.");
                return true;
            }
        } else if(cmd.getName().equalsIgnoreCase("afmreload")) {
            this.reloadConfig();
            sender.sendMessage(ChatColor.GREEN+"AFMCP "+ChatColor.WHITE+" > Конфиг был успещно перезагружен.");
        } else if(cmd.getName().equalsIgnoreCase("tokens")) {
            if(sender instanceof Player) {
                String playerBal = eco.getBalance(sender.getName());

                if(playerBal == null) {
                    sender.sendMessage(ChatColor.RED+"AFMEco "+ChatColor.WHITE+"> Произошла ошибка при выполнении команды.");
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN+"AFMEco "+ChatColor.WHITE+"> Ваш баланс: "+ChatColor.GREEN+playerBal+" токенов"+ChatColor.WHITE+".");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED+"Данная команда может быть выполнена только игроком.");
                return true;
            }
        } else if(cmd.getName().equalsIgnoreCase("notify")) {
            if(args.length > 0 && String.join(" ", args).length() <= 48) {
                JavaPlugin plugin = this;

                for(Player p : Bukkit.getOnlinePlayers()) {
                    ByteArrayOutputStream b = new ByteArrayOutputStream();
                    DataOutputStream out = new DataOutputStream(b);

                    String str = String.join(" ", args);

                    try {
                        out.writeUTF(ChatColor.translateAlternateColorCodes('&', str));
                    } catch(IOException e) {
                        System.out.println("Error sending FactionsShow data.");
                    }

                    p.sendPluginMessage(this, "FactionsShow", b.toByteArray());
                    p.playSound(p.getLocation(), Sound.NOTE_PLING, 10, -3);
                }

                Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                    public void run() {
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            ByteArrayOutputStream b = new ByteArrayOutputStream();
                            DataOutputStream out = new DataOutputStream(b);

                            try {
                                out.writeUTF("");
                            } catch(IOException e) {
                                System.out.println("Error sending FactionsShow data.");
                            }

                            p.sendPluginMessage(plugin, "FactionsShow", b.toByteArray());
                        }
                    }
                }, 100L);

                return true;
            } else {
                return false;
            }
        }

        return false;
    }
}
