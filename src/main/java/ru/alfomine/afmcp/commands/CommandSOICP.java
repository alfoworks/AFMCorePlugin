package ru.alfomine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginStatics;
import ru.alfomine.afmcp.tablist.WrappedTabListEntry;

import java.util.ArrayList;

public class CommandSOICP extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
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

            if (args.get(1).equalsIgnoreCase("gettablist")) {
                sender.sendMessage("===============TabList===============");

                for (WrappedTabListEntry entry : AFMCorePlugin.tabList.getEntries()) {
                    sender.sendMessage(String.format("%s, gm: %s, latency: %s", entry.name, entry.gameMode.name(), String.valueOf(entry.latency)));
                }

                sender.sendMessage("=====================================");
            } else if (args.get(1).equalsIgnoreCase("fparticles")) {
                if (!(sender instanceof Player)) {
                    sendErrorMessage(sender, "Эта команда не может быть выполнена от консоли.");
                    return true;
                }

                Player player = (Player) sender;

                if (!PluginStatics.debugFlightParticlesPlayers.remove(player)) {
                    PluginStatics.debugFlightParticlesPlayers.add(player);
                }

                sendMessage(sender, String.format("Переключено: %s", PluginStatics.debugFlightParticlesPlayers.contains(player)));
            } else if (args.get(1).equalsIgnoreCase("rtx")) {
                if (!(sender instanceof Player)) {
                    sendErrorMessage(sender, "Эта команда не может быть выполнена от консоли.");
                    return true;
                }

                Player player = (Player) sender;

                if (!PluginStatics.debugRtxPlayers.remove(player)) {
                    PluginStatics.debugRtxPlayers.add(player);
                }

                sendMessage(sender, String.format("Переключено: %s", PluginStatics.debugRtxPlayers.contains(player)));
            } else {
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
        return "SOICP";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.GREEN;
    }
}
