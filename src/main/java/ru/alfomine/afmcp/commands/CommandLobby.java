package ru.alfomine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginConfig;
import ru.alfomine.afmcp.util.LocationUtil;

import java.util.ArrayList;

public class CommandLobby extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) throws Exception {
        if (args.size() < 1) {
            sendMessage(sender, "Список подкоманд:");

            sender.sendMessage(String.format("%s%s %s- %s%s", ChatColor.GOLD, "setspawn", ChatColor.WHITE, ChatColor.LIGHT_PURPLE, "установить место спавна"));

            return true;
        }

        String subCommand = args.get(0);

        if (subCommand.equalsIgnoreCase("setspawn")) {
            PluginConfig.lobbySpawnLocation = LocationUtil.toString(((Player) sender).getLocation());
            AFMCorePlugin.getPlugin().saveConfig();

            sendMessage(sender, "Позиция спавна успешно установлена.");
        }

        return true;
    }

    @Override
    public String getName() {
        return "Lobby";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_GREEN;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
