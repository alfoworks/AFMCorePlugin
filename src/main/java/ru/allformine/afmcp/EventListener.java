package ru.allformine.afmcp;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import org.bukkit.ChatColor;
import org.bukkit.Location;//123
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.discord.Discord;
import java.util.Set;
import static ru.allformine.afmcp.References.frozenPlayers;

class EventListener implements Listener {
    private JavaPlugin plugin;

    EventListener(JavaPlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    //Сообщение в дискорд о входе/выходе игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), true, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), false, plugin);
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

        for (String item : event.getMessage().toLowerCase().split(" ")) {
            if(plugin.getConfig().getStringList("discord.logger.triggerWords").contains(item)) {
                message.insert(0, "Обнаруженно триггер-слово: " + item + " @here\n");
            }
        }

        Discord.sendMessage(message.toString(), true, event.getSender().getName(), logLevel, plugin);
    }

    //Сообщение в дискорд о том, что игрок получил ачивку (здесь какой-то непонятный краш)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        try {
            String message = "Игрок получил достижение **"+event.getAchievement().name()+"**.";

            Discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1, plugin);
        } catch(Exception e) {
            System.out.println("A client-side error occurred when was trying to log an achievement"); //при получении НЕванильной ачивки какая-то ошибка..
        }
    }

    //Сообщение в дискорд о том, что игрок выполнил команду (причем неважно, успешно или нет)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(!event.isCancelled()) { //Возможно это поможет от логирования несуществующих комманд, но я хз чот...
            String[] message = event.getMessage().split(" ");
            String command = message[0];
            if(!plugin.getConfig().getStringList("discord.logger.ignoredCommands").contains(command)) {
                Discord.sendMessage("Игрок выполнил команду **"+event.getMessage()+"**", true, event.getPlayer().getDisplayName(), 2, plugin);
            }
        }
    }

    //Сообщение в дискорд о смерти игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Discord.sendMessage(event.getDeathMessage(), true, event.getEntity().getDisplayName(), 1, plugin);
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

        // Запрещает делать любые действия с аномалией, когда она в виде айтема (на всяикй пожарный)
        if(event.hasBlock()) {
            if(event.getMaterial().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW+"Вы не можете совершать действия с данным блоком.");

                event.setCancelled(true);
            }
        }

        // Запрещает ломать аномалию ведром воды, если ведром воды нажали ПРЯМО на нее.
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.YELLOW+"Вы не можете ломать этот блок.");

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    // Запрещает ломать аномалию ведром воды, если ведром воды нажали на блок, расположенный вплотную к аномалии
    public void onBlockPlace(BlockPlaceEvent event) {
        if(event.getBlockAgainst().getType().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.YELLOW+"Вы не можете сломать данный блок.");

            event.setCancelled(true);
        }
    }
}
