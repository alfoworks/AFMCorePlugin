package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import java.util.ArrayList;

public class AFMCMCommand {
    public String name = "afmcp";
    public String displayName = "TestCommand";
    public ChatColor commandChatColor = ChatColor.BLUE;

    public boolean run(ArrayList<String> args, CommandSender sender) {
        return true;
    }

    public void reply(CommandSender sender, String text) {
        sender.sendMessage(commandChatColor+displayName+ChatColor.RESET+" > "+text);
    }
}
