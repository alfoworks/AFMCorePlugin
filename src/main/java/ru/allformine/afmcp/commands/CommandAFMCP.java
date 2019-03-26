package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class CommandAFMCP extends AFMCPCommand {
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
        return ChatColor.BLACK;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        int test = 0;

        System.out.println(90 / test);

        return true;
    }
}
