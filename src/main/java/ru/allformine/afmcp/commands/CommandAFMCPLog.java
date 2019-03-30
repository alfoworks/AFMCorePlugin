package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.References;
import java.util.ArrayList;

public class CommandAFMCPLog extends AFMCPCommand {
    @Override
    public String getName() {
        return "afmcplog";
    }

    @Override
    public String getDisplayName() {
        return "DiscordLogging";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.AQUA;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if (args.size() > 0 && (args.get(0).equals("true") || args.get(0).equals("false"))) {
            References.log = args.get(0).equals("true");

            reply(sender, ChatColor.AQUA + "Логгирование было успешно переключено.");
            return true;
        } else {
            return false;
        }
    }
}