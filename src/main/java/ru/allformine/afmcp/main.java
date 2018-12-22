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
import com.dthielke.herochat.Chatter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import ru.allformine.afmcp.CFNTasks.CFNTaskSpace;
import ru.allformine.afmcp.CFNTasks.CFNTaskTechno;
import ru.allformine.afmcp.net.discord.discord;
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

    private ArrayList<Player> frozenPlayers = new ArrayList<>();

    public void onEnable() {
        PluginManager manager = this.getServer().getPluginManager();

        manager.registerEvents(this,this);
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

        discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1, this); //отправляем в дс сообщеньку, что сервак врублен.
    }

    //Сообщение в дискорд о входе/выходе игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), true, this);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), false, this);
    }

    //Сообщение в дискорд из чата.
    @EventHandler(priority = EventPriority.MONITOR)
    public void ChannelChatEvent(ChannelChatEvent event) {
        String channelName = event.getChannel().getName();
        int logLevel = 1;
        StringBuilder message = new StringBuilder(event.getMessage());

        if(event.getChannel().getName().equals("Local")) {
            Location location = event.getSender().getPlayer().getLocation();
            int x = (int) Math.round(location.getX());
            int y = (int) Math.round(location.getY());
            int z = (int) Math.round(location.getZ());
            message.insert(0, "{" + String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z) + "} ");

            logLevel = 2;
        } else if(event.getChannel().getName().contains("convo")) {
            //Небольшой костыль для получения ника игрока, которому отправляется личное сообщение.
            Set<Chatter> chatMembers = event.getChannel().getMembers();
            for(Chatter chatter : chatMembers) {
                if(!chatter.getPlayer().getDisplayName().equals(event.getSender().getPlayer().getDisplayName())) {
                    channelName = "Личное сообщение игроку "+chatter.getPlayer().getDisplayName();
                    break;
                }
            }

            logLevel = 2;
        }

        message.insert(0, "[" + channelName + "] ");

        for (String item : triggerWords) {
            if(event.getMessage().toLowerCase().contains(item)) {
                message.insert(0, "Обнаруженно триггер-слово: " + item + " @here\n");
            }
        }

        discord.sendMessage(message.toString(), true, event.getSender().getName(), logLevel, this);
    }

    //Сообщение в дискорд о том, что игрок получил ачивку (здесь какой-то непонятный краш)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        String message = "Игрок получил достижение **"+event.getAchievement().name()+"**.";

        discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1, this);
    }

    //Сообщение в дискорд о том, что игрок выполнил команду (причем неважно, успешно или нет)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(!event.isCancelled()) { //Возможно это поможет от логирования несуществующих комманд, но я хз чот...
            String[] message = event.getMessage().split(" ");
            String command = message[0];
            if(!Arrays.asList(notLoggedCommands).contains(command)) {
                discord.sendMessage("Игрок выполнил команду **"+event.getMessage()+"**", true, event.getPlayer().getDisplayName(), 2, this);
            }
        }
    }

    //Сообщение в дискорд о смерти игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        discord.sendMessage(event.getDeathMessage(), true, event.getEntity().getDisplayName(), 1, this);
    }

    //Сообщение в дискорд о том, что сервер упал.
    public void onDisable() {
        discord.sendMessageSync("Сервер упал!", false, "TechInfo", 1);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вы заморожены!");
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if(frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED+"Freeze "+ChatColor.RESET+"> вы заморожены!");
        }

        if(event.hasBlock()) { // На всякий случай
            if(event.getMaterial().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW+"Вы не можете совершать действия с данным блоком.");

                event.setCancelled(true);
            }
        }

        // Фикс ломания аномалии
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().name().equals("MO_GRAVITATIONAL_ANOMALY")) {
            event.getPlayer().sendMessage(ChatColor.YELLOW+"Вы не можете ломать этот блок.");

            event.setCancelled(true);
        }
    }

    //Ебанные команды
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("afmcp")) {
            sender.sendMessage("Твоя мать шлюха");
            return true;
        } else if(cmd.getName().equalsIgnoreCase("freeze")) {
            if(args.length > 0) {
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
