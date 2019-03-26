package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class AFMCPCommand {
    public String getName() {
        return "test";
    }

    public String getDisplayName() {
        return "test";
    }

    public ChatColor getCommandChatColor() {
        return ChatColor.AQUA;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        return true;
    }

    public void reply(CommandSender sender, String text) {
        sender.sendMessage(getCommandChatColor()+getDisplayName()+ChatColor.RESET+" > "+text);
    }
}
