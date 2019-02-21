package ru.allformine.afmcp;

import com.dthielke.herochat.ChannelChatEvent;
import com.dthielke.herochat.Chatter;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.packet.Ambient;

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
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), true);

        if (plugin.getConfig().get("playerdata." + event.getPlayer().getName()) == null) {
            plugin.getConfig().set("playerdata." + event.getPlayer().getName() + ".giftGiven", false);

            System.out.println("Created configuration section for player " + event.getPlayer().getName());
        }

        RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
        if (!regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions().isEmpty()) {
            String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions()).getId();
            String url = plugin.getConfig().getString("ambient_data." + playerRegionName + ".url");

            if (url != null && (References.playerCurrentMusic.get(event.getPlayer()) == null || !References.playerCurrentMusic.get(event.getPlayer()).equals(playerRegionName))) {
                Ambient.sendAmbientMusicPacket(false, event.getPlayer(), url);
                References.playerCurrentMusic.put(event.getPlayer(), playerRegionName);
            } else if (url == null && References.playerCurrentMusic.get(event.getPlayer()) != null) {
                Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
                References.playerCurrentMusic.remove(event.getPlayer());
            }
        } else if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
            References.playerCurrentMusic.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        PlayerQuitJoin.sendPlayerQuitJoinMessage(event.getPlayer(), false);

        if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
            References.playerCurrentMusic.remove(event.getPlayer());
        }
    }

    //Сообщение в дискорд из чата.
    @EventHandler(priority = EventPriority.MONITOR)
    public void ChannelChatEvent(ChannelChatEvent event) {
        String channelName = event.getChannel().getName();
        int logLevel = 1;
        StringBuilder message = new StringBuilder(event.getMessage());

        if (event.getChannel().getName().equals("Local")) {
            Location location = event.getSender().getPlayer().getLocation();
            int x = (int) Math.round(location.getX());
            int y = (int) Math.round(location.getY());
            int z = (int) Math.round(location.getZ());
            message.insert(0, "{" + String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z) + "} ");

            logLevel = 2;
        } else if (event.getChannel().getName().contains("convo")) {
            //Небольшой костыль для получения ника игрока, которому отправляется личное сообщение.
            Set<Chatter> chatMembers = event.getChannel().getMembers();
            for (Chatter chatter : chatMembers) {
                if (!chatter.getPlayer().getDisplayName().equals(event.getSender().getPlayer().getDisplayName())) {
                    channelName = "Личное сообщение игроку " + chatter.getPlayer().getDisplayName();
                    break;
                }
            }

            logLevel = 2;
        }

        message.insert(0, "[" + channelName + "] ");

        for (String item : plugin.getConfig().getStringList("discord.logger.triggerWords")) {
            if (event.getMessage().toLowerCase().contains(item)) {
                message.insert(0, "Обнаруженно триггер-слово: " + item + " @here\n");
            }
        }

        Discord.sendMessage(message.toString(), true, event.getSender().getName(), logLevel);
    }

    //Сообщение в дискорд о том, что игрок получил ачивку (здесь какой-то непонятный краш)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        try {
            String message = "Игрок получил достижение **" + event.getAchievement().name() + "**.";

            Discord.sendMessage(message, true, event.getPlayer().getDisplayName(), 1);
        } catch (Exception e) {
            System.out.println("The achievement is not vanilla or some other bad thing happened."); //при получении НЕванильной ачивки какая-то ошибка..
        }
    }

    //Сообщение в дискорд о том, что игрок выполнил команду (причем неважно, успешно или нет)
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (!event.isCancelled()) { //Возможно это поможет от логирования несуществующих комманд, но я хз чот...
            String[] message = event.getMessage().split(" ");
            String command = message[0];
            if (!plugin.getConfig().getStringList("discord.logger.ignoredCommands").contains(command)) {
                Discord.sendMessage("Игрок выполнил команду **" + event.getMessage() + "**", true, event.getPlayer().getDisplayName(), 2);
            }
        }
    }

    //Сообщение в дискорд о смерти игрока
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getDisplayName().equals("Sila_Zemli")) {
            event.setDeathMessage(event.getEntity().getDisplayName() + " умер от СПИДа");
        }

        Discord.sendMessage(event.getDeathMessage(), true, event.getEntity().getDisplayName(), 1);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }

        if (!event.isCancelled()) {
            RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
            if (!regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions().isEmpty()) {
                String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions()).getId();
                String url = plugin.getConfig().getString("ambient_data." + playerRegionName + ".url");

                if (url != null && (References.playerCurrentMusic.get(event.getPlayer()) == null || !References.playerCurrentMusic.get(event.getPlayer()).equals(playerRegionName))) {
                    Ambient.sendAmbientMusicPacket(false, event.getPlayer(), url);
                    References.playerCurrentMusic.put(event.getPlayer(), playerRegionName);
                } else if (url == null && References.playerCurrentMusic.get(event.getPlayer()) != null) {
                    Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
                    References.playerCurrentMusic.remove(event.getPlayer());
                }
            } else if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
                References.playerCurrentMusic.remove(event.getPlayer());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);

            event.getPlayer().sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы заморожены!");
        }

        // Запрещает делать любые действия с аномалией, когда она в виде айтема (на всяикй пожарный)
        if (event.hasBlock()) {
            if (event.getMaterial().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "Вы не можете совершать действия с данным блоком.");

                event.setCancelled(true);
            }
        }

        // Запрещает ломать аномалию ведром воды, если ведром воды нажали ПРЯМО на нее.
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getClickedBlock().getType().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Вы не можете ломать этот блок.");

            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    // Походу не работает
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.getBlockAgainst().getType().name().equals("MO_GRAVITATIONAL_ANOMALY") && !event.getPlayer().isOp()) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "Вы не можете сломать данный блок.");

            event.setCancelled(true);
        }
    }

    /* НЕДОПИСАНО!!!
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChunkUnload(ChunkUnloadEvent event) {
        String x = String.valueOf(event.getChunk().getX());
        String z = String.valueOf(event.getChunk().getZ());
        if(plugin.getConfig().get("loaders."+x+"_"+z) != null) {

        }
    }
    **/

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (plugin.getConfig().getBoolean("server_maintenance.enabled") && !event.getPlayer().hasPermission("afmcp.staff")) {
            event.setKickMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("server_maintenance.kickMessage")));
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
        if (!regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions().isEmpty()) {
            String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions()).getId();
            String url = plugin.getConfig().getString("ambient_data." + playerRegionName + ".url");

            if (url != null && (References.playerCurrentMusic.get(event.getPlayer()) == null || !References.playerCurrentMusic.get(event.getPlayer()).equals(playerRegionName))) {
                Ambient.sendAmbientMusicPacket(false, event.getPlayer(), url);
                References.playerCurrentMusic.put(event.getPlayer(), playerRegionName);
            } else if (url == null && References.playerCurrentMusic.get(event.getPlayer()) != null) {
                Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
                References.playerCurrentMusic.remove(event.getPlayer());
            }
        } else if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
            References.playerCurrentMusic.remove(event.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        RegionManager regionManager = WGBukkit.getRegionManager(event.getPlayer().getWorld());
        if (!regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions().isEmpty()) {
            String playerRegionName = Util.getLastElement(regionManager.getApplicableRegions(event.getPlayer().getLocation()).getRegions()).getId();
            String url = plugin.getConfig().getString("ambient_data." + playerRegionName + ".url");

            if (url != null && (References.playerCurrentMusic.get(event.getPlayer()) == null || !References.playerCurrentMusic.get(event.getPlayer()).equals(playerRegionName))) {
                Ambient.sendAmbientMusicPacket(false, event.getPlayer(), url);
                References.playerCurrentMusic.put(event.getPlayer(), playerRegionName);
            } else if (url == null && References.playerCurrentMusic.get(event.getPlayer()) != null) {
                Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
                References.playerCurrentMusic.remove(event.getPlayer());
            }
        } else if (References.playerCurrentMusic.get(event.getPlayer()) != null) {
            Ambient.sendAmbientMusicPacket(true, event.getPlayer(), "");
            References.playerCurrentMusic.remove(event.getPlayer());
        }
    }
}
