package ru.allformine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.References;

import java.util.ArrayList;

public class CommandAFMRestart extends AFMCPCommand {
    @Override
    public String getName() {
        return "afmcp";
    }

    @Override
    public String getDisplayName() {
        return "AFMCP";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.RED;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        reply(sender, "Перезагрузка сервера...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.kickPlayer(ChatColor.LIGHT_PURPLE+"Сервер ушёл на рестарт! Вернемся через минуту <3");
        }

        References.serverRestarting = true;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/stop");

        return true;
    }
}
