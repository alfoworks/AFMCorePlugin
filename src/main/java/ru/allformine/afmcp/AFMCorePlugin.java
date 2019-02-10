package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.kitteh.vanish.staticaccess.VanishNoPacket;
import org.kitteh.vanish.staticaccess.VanishNotLoadedException;
import ru.allformine.afmcp.net.discord.Discord;
import ru.allformine.afmcp.net.eco.Eco;
import ru.allformine.afmcp.net.http.HTTPServer;
import ru.allformine.afmcp.notify.Notify;
import ru.allformine.afmcp.tasks.TPSWatchdog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static ru.allformine.afmcp.References.frozenPlayers;

public class AFMCorePlugin extends JavaPlugin implements PluginMessageListener {
    private Random random = new Random(); //Создаем экземпляр класса рандома на весь плагин, дабы он был без повторений
    private HTTPServer apiServer = new HTTPServer();

    public static Plugin getPlugin() {
        return Bukkit.getPluginManager().getPlugin("AFMCorePlugin");
    }

    public void onEnable() {
        new EventListener(this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "Notify");
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "C234Fb");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "C234Fb", this);

        this.saveDefaultConfig();

        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TPSWatchdog(), 100L, 1L);
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
            for (byte _byte : message) {
                System.out.println(_byte);
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
        } else if (cmd.getName().equalsIgnoreCase("notify")) {
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
        } else if (cmd.getName().equalsIgnoreCase("gift")) {
            if (sender instanceof Player) {
                if (!this.getConfig().getBoolean("playerdata." + sender.getName() + ".giftGiven")) {
                    String[] kits = new String[]{"ny1", "ny2", "ny1", "ny1"};
                    String kit = kits[random.nextInt(kits.length)];
                    Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "kitgive " + sender.getName() + " " + kit + " 1");
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Gifts " + ChatColor.WHITE + "> Отлично! Для получения своего подарка напишите /kit " + kit);

                    this.getConfig().set("playerdata." + sender.getName() + ".giftGiven", true);
                    this.saveConfig();
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Gifts " + ChatColor.WHITE + "> Вы уже получили подарок.");
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Данная команда может быть выполнена только игроком.");
            }
            return true;
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
        }

        return false;
    }
}
