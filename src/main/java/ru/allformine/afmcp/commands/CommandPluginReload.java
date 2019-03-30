package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.AFMCorePlugin;
import java.util.ArrayList;

public class CommandPluginReload extends AFMCPCommand {
    @Override
    public String getName() {
        return "afmreload";
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
        AFMCorePlugin.getPlugin().reloadConfig();

        reply(sender, "Плагин был перезапущен.");

        return true;
    }
}
