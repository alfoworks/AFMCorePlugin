package ru.alfomine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;

public class CommandRawBC extends CustomCommand {
    @Override
    public String getName() {
        return "RawBC";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_AQUA;
    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        if (args.size() > 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));

            sendMessage(sender, "Сообщение отправлено");

            return true;
        } else {
            return false;
        }
    }
}
