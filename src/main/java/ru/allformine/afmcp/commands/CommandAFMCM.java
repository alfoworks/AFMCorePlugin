package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class CommandAFMCM extends AFMCMCommand {
    public String name = "afmcm";
    public String displayName = "AFMCM";
    public ChatColor commandChatColor = ChatColor.BLACK;

    public boolean run(ArrayList<String> args, CommandSender sender) {
        reply(sender, "Плагин работает!");

        return true;
    }
}
