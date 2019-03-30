package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.packet.Notify;
import java.util.ArrayList;

public class CommandNotify extends AFMCPCommand {
    @Override
    public String getName() {
        return "notify";
    }

    @Override
    public String getDisplayName() {
        return "Notify";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.BLUE;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if (args.size() > 0 && String.join(" ", args).length() <= 48) {
            Notify.notifyAll(ChatColor.translateAlternateColorCodes('&', String.join(" ", args)));
            reply(sender, "Сообщение было успешно отправлено!");

            return true;
        } else {
            return false;
        }
    }
}