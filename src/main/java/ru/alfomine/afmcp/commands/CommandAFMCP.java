package ru.alfomine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginStatics;
import ru.alfomine.afmcp.tablist.TabListUpdateTask;
import ru.alfomine.afmcp.tablist.WrappedTabListEntry;

import java.util.ArrayList;

public class CommandAFMCP extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) throws Exception {
        if (args.size() < 1) {
            sendMessage(sender, String.format("%sSOICorePlugin%s, %sv0.1%s, by %sIterator%s. (C) %sALFO:WorkS%s.", getColor(),
                    ChatColor.WHITE,
                    ChatColor.GOLD,
                    ChatColor.WHITE,
                    ChatColor.DARK_PURPLE,
                    ChatColor.WHITE,
                    ChatColor.LIGHT_PURPLE,
                    ChatColor.WHITE));

            if (!(sender instanceof Player)) {
                sender.sendMessage("https://mine.alfo.ws");
            } else {
                Bukkit.getServer().dispatchCommand(
                        Bukkit.getConsoleSender(),
                        String.format("tellraw %s {\"text\":\"https://mine.alfo.ws\",\"underlined\":true,\"color\":\"gray\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://mine.alfo.ws\"}}", sender.getName()));
            }
        } else if (args.get(0).equalsIgnoreCase("reload")) {
            AFMCorePlugin.getPlugin().reloadConfig();

            sendMessage(sender, "Конфиг был успешно перезагружен.");
        } else if (args.get(0).equalsIgnoreCase("debug")) {
            if (args.size() < 2) {
                sendMessage(sender, "Nope");

                return true;
            }
            switch (args.get(1).toLowerCase()) {
                case "gettablist":
                    sender.sendMessage("===============TabList===============");

                    for (WrappedTabListEntry entry : AFMCorePlugin.tabList.getEntries()) {
                        sender.sendMessage(String.format("%s, gm: %s, latency: %s", entry.name, entry.gameMode.name(), String.valueOf(entry.latency)));
                    }

                    sender.sendMessage("=====================================");
                    break;
                case "fparticles":
                    if (!(sender instanceof Player)) {
                        sendErrorMessage(sender, "Эта команда не может быть выполнена от консоли.");
                        return true;
                    }

                    Player player = (Player) sender;

                    if (!PluginStatics.debugFlightParticlesPlayers.remove(player)) {
                        PluginStatics.debugFlightParticlesPlayers.add(player);
                    }

                    sendMessage(sender, String.format("Переключено: %s", PluginStatics.debugFlightParticlesPlayers.contains(player)));
                    break;
                case "exceptiontest":
                    throw new Exception("Text exception from debug command");
                case "ondisable":
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        plugin.onDisable();
                    }

                    sendMessage(sender, "Все \"выключено\"");
                    break;
                case "newyear":
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        Bukkit.getPluginManager().disablePlugin(plugin);
                    }

                    Bukkit.getOnlinePlayers().forEach(player1 -> {
                        player1.sendMessage(ChatColor.YELLOW + "AllForMine" + ChatColor.WHITE + " > С Новым Годом!!!");
                        player1.getWorld().strikeLightning(player1.getLocation());
                    });

                    sendMessage(sender, "Все выключено");
                    break;
                case "retranslate":
                    PluginStatics.debugRetranslateEnabled = !PluginStatics.debugRetranslateEnabled;

                    sendMessage(sender, String.format("Переключено: %s", PluginStatics.debugRetranslateEnabled));
                    break;
                case "tablist":
                    if(args.size() < 3) {
                        AFMCorePlugin.tabList.testSendPacket();

                        sendMessage(sender, "Пакеты отправлены!");
                    }else{
                        String mode = args.get(2);
                        switch (mode){
                            case "3": TabListUpdateTask.mode = 3; break;
                            case "2": TabListUpdateTask.mode = 2; break;
                            case "1": TabListUpdateTask.mode = 1; break;
                            default: TabListUpdateTask.mode = 0; break;
                        }
                        sendMessage(sender, "Режим: " + TabListUpdateTask.mode);
                    }
                    break;
                default:
                    sendMessage(sender, "Неизвестная подкоманда. Да и вообще, вылези из дебага. Это не тебе сделано)");

                    return true;
            }
        } else {
            return false;
        }

        return true;
    }

    @Override
    public String getName() {
        return "AFMCP";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GREEN;
    }
}
