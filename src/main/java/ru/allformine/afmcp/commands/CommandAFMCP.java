package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class CommandAFMCP extends AFMCPCommand {
    public String name = "afmcp";
    public String displayName = "AFMCP";
    public ChatColor commandChatColor = ChatColor.BLACK;

    public boolean run(ArrayList<String> args, CommandSender sender) {
        reply(sender, "Плагин работает!");

        return true;
    }
}
