package ru.allformine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class CommandRawBC extends AFMCPCommand {
    @Override
    public String getName() {
        return "rawbc";
    }

    @Override
    public String getDisplayName() {
        return "RawBC";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.DARK_AQUA;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if (args.size() > 0) {
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));

            reply(sender, "Сообщение отправлено");

            return true;
        } else {
            return false;
        }
    }
}
