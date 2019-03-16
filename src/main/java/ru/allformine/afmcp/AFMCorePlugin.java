package ru.allformine.afmcp;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.net.eco.Eco;
import ru.allformine.afmcp.net.http.HTTPServer;
import ru.allformine.afmcp.packet.Notify;

import java.util.ArrayList;
import java.util.Arrays;

import static ru.allformine.afmcp.References.frozenPlayers;

public class AFMCorePlugin extends JavaPlugin implements PluginMessageListener {
    private HTTPServer apiServer = new HTTPServer();

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("AFMCorePlugin");
    }

    public void onEnable() {
        new EventListener(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Notify");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "C234Fb");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "C234Fb", this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "ambient");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "territoryshow");

        this.saveDefaultConfig();

        Bukkit.getServer().getScheduler().runTaskAsynchronously(this, apiServer);

        try { //Проверялка на то, есть ли плагин на ваниш.
            //noinspection deprecation
            ProtocolHandler.vanishManager = VanishNoPacket.getManager();
        } catch (VanishNotLoadedException ex) {
            System.out.println("Can't find VanishNoPacket.");
        }

        ProtocolHandler.startHandler();

        Discord.sendMessage("Сервер поднялся!", false, "TechInfo", 1); //отправляем в дс сообщеньку, что сервак врублен.
    }

    //Сообщение в дискорд о том, что сервер упал.
    public void onDisable() {
        Discord.sendMessageSync("@everyone Сервер упал!", false, "TechInfo", 1);
    }

    //Скриншотер
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (channel.equals("C234Fb")) {
            if (apiServer.playerScreenshotData.get(player) != null) {
                message = Util.trim(message);
                message = Arrays.copyOf(message, message.length - 1);

                byte[] prevArr = apiServer.playerScreenshotData.get(player);
                apiServer.playerScreenshotData.put(player, ArrayUtils.addAll(prevArr, message));

                if (message.length < 10240) {
                    apiServer.playerScreenshotConfirmation.put(player, true);
                }
            }
        }
    }

    //Ебанные команды
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("afmcp")) {
            sender.sendMessage("Твоя мать шлюха");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("freeze")) {
            if (args.length > 0) {
                @SuppressWarnings("deprecation")
                Player player = Bukkit.getPlayer(args[0]); //да мне похуй, что оно блядь не поддерживается. МНЕ ПОХУЙ!

                if (player != null) {
                    if (!frozenPlayers.contains(player)) {
                        frozenPlayers.add(player);

                        sender.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы успешно заморозили этого игрока.");
                        player.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вас заморозили.");
                    } else {
                        frozenPlayers.remove(player);

                        sender.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вы успешно разморозили этого игрока.");
                        player.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> вас разморозили.");
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Freeze " + ChatColor.RESET + "> игрок не найден.");
                }
            } else {
                return false;
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("rawbc")) {
            if (args.length > 0) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));

                return true;
            } else {
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("vip")) {
            if (sender instanceof Player) {
                if (args.length > 0 && this.getConfig().contains("vips." + args[0])) {
                    String playerBal = Eco.getBalance(sender.getName());
                    int cost = this.getConfig().getInt("vips." + args[0] + ".cost");

                    if (playerBal == null) {
                        sender.sendMessage(ChatColor.RED + "AFMEco " + ChatColor.WHITE + "> Произошла ошибка при выполнении команды.");
                        return true;
                    }

                    if (Integer.valueOf(playerBal) < cost) {
                        int needed = cost - Integer.valueOf(playerBal);

                        sender.sendMessage(ChatColor.RED
                                + "AFMEco "
                                + ChatColor.WHITE
                                + "> У вас недостаточно токенов. Вам нужно еще "
                                + ChatColor.RED
                                + String.valueOf(needed)
                                + " токенов" + ChatColor.WHITE
                                + ".");
                        return true;
                    }

                    sender.sendMessage(ChatColor.GREEN + "AFMEco " + ChatColor.WHITE + "> Вы успешно приобрели привелегию. Спасибо!");
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givevip " + sender.getName() + " " + args[0] + " 30");
                    Discord.sendMessage("@everyone\nИгрок " + sender.getName() + " приобрёл подписку **" + args[0] + "**!!!", false, "DonationAlerts", 1);

                    Eco.rem(sender.getName(), String.valueOf(cost), this);

                    return true;
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "AFMEco " + ChatColor.WHITE + "> Список доступных привелегий:");

                    for (String key : this.getConfig().getConfigurationSection("vips").getKeys(false)) {
                        sender.sendMessage(ChatColor.YELLOW
                                + key
                                + ChatColor.WHITE
                                + " - "
                                + ChatColor.YELLOW
                                + String.valueOf(this.getConfig().getInt("vips." + key + ".cost"))
                                + " токенов");
                    }
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Данная команда может быть выполнена только игроком.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("afmreload")) {
            this.reloadConfig();
            sender.sendMessage(ChatColor.GREEN + "AFMCP " + ChatColor.WHITE + " > Конфиг был успещно перезагружен.");
            return true;
        } else if (cmd.getName().equalsIgnoreCase("tokens")) {
            if (sender instanceof Player) {
                String playerBal = Eco.getBalance(sender.getName());

                if (playerBal == null) {
                    sender.sendMessage(ChatColor.RED + "AFMEco " + ChatColor.WHITE + "> Произошла ошибка при выполнении команды.");
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN + "AFMEco " + ChatColor.WHITE + "> Ваш баланс: " + ChatColor.GREEN + playerBal + " токенов" + ChatColor.WHITE + ".");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Данная команда может быть выполнена только игроком.");
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("packet")) {
            if (args.length > 0 && String.join(" ", args).length() <= 48) {
                Notify.notifyAll(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
                sender.sendMessage(ChatColor.BLUE + "Notify " + ChatColor.WHITE + "> Сообщение было успешно отправлено!");

                return true;
            } else {
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("notifyplayer")) {
            if (args.length > 0) {
                Player player = Bukkit.getPlayer(args[0]);

                if (player != null) {
                    String text = String.join(" ", new ArrayList<>(Arrays.asList(args)).remove(0));
                    if (text.length() <= 48) {
                        Notify.notifyPlayer(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)), player);
                        sender.sendMessage(ChatColor.BLUE + "Notify " + ChatColor.WHITE + "> Сообщение было успешно отправлено!");
                    } else {
                        return false;
                    }
                } else {
                    sender.sendMessage(ChatColor.BLUE + "Notify " + ChatColor.RESET + "> игрок не найден.");
                }

                return true;
            } else {
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("afmcplog")) {
            if (args.length > 0 && (args[0].equals("true") || args[0].equals("false"))) {
                References.log = args[0].equals("true");

                sender.sendMessage(ChatColor.AQUA + "DiscordLogging " + ChatColor.WHITE + "> Логгирование было успешно переключено.");
                return true;
            } else {
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("maintenance")) {
            if (args.length > 0 && (args[0].equals("true") || args[0].equals("false"))) {
                this.getConfig().set("server_maintenance.enabled", args[0].equals("true"));
                sender.sendMessage(ChatColor.DARK_AQUA + "Maintenance " + ChatColor.WHITE + "> Режим тех. работ был переключен.");

                for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                    if (!p.hasPermission("afmcp.staff")) {
                        p.kickPlayer(ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("server_maintenance.kickMessage")));
                    }
                }
                return true;
            } else {
                return false;
            }
        } else if (cmd.getName().equalsIgnoreCase("ambient")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "AmbientMusic " + ChatColor.WHITE + "> Данная команда может быть выполнена только игроком.");
                return true;
            }

            if (args.length < 2) {
                return false;
            }

            Player player = (Player) sender;
            RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());

            if (regionManager == null) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "AmbientMusic " + ChatColor.WHITE + "> Произошла ошибка.");
                return true;
            }

            if (regionManager.getRegion(args[1]) == null) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "AmbientMusic " + ChatColor.WHITE + "> Регион с таким именем не найден.");
                return true;
            }

            String rg_name = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    return false;
                }

                this.getConfig().set("ambient_data." + rg_name + ".url", args[2]);
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (this.getConfig().getString("ambient_data." + rg_name + ".url") == null) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "AmbientMusic " + ChatColor.WHITE + "> Запись не найдена.");
                    return true;
                }

                this.getConfig().set("ambient_data." + rg_name + ".url", null);
            } else {
                return false;
            }

            this.saveConfig();
            sender.sendMessage(ChatColor.DARK_PURPLE + "AmbientMusic " + ChatColor.WHITE + "> Действие было выполнено.");

            return true;
        } else if (cmd.getName().equalsIgnoreCase("rgname")) {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "RGName " + ChatColor.WHITE + "> Данная команда может быть выполнена только игроком.");
                return true;
            }

            if (args.length < 2) {
                return false;
            }

            Player player = (Player) sender;
            RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());

            if (regionManager == null) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "RGName " + ChatColor.WHITE + "> Произошла ошибка.");
                return true;
            }

            if (regionManager.getRegion(args[1]) == null) {
                sender.sendMessage(ChatColor.DARK_PURPLE + "RGName " + ChatColor.WHITE + "> Регион с таким именем не найден.");
                return true;
            }

            String rg_name = args[1].toLowerCase();

            if (args[0].equalsIgnoreCase("add")) {
                if (args.length < 3) {
                    return false;
                }

                this.getConfig().set("rgname_data." + rg_name + ".name", args[2]);
            } else if (args[0].equalsIgnoreCase("remove")) {
                if (this.getConfig().getString("rgname_data." + rg_name + ".name") == null) {
                    sender.sendMessage(ChatColor.DARK_PURPLE + "RGName " + ChatColor.WHITE + "> Запись не найдена.");
                    return true;
                }

                this.getConfig().set("rgname_data." + rg_name + ".name", null);
            } else {
                return false;
            }

            this.saveConfig();
            sender.sendMessage(ChatColor.DARK_PURPLE + "RGName " + ChatColor.WHITE + "> Действие было выполнено.");

            return true;
        }

        return false;
    }
}
