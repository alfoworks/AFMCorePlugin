package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.References;
import java.util.ArrayList;

/* Некий костыль, чтобы при перезагрузке сервера из ASW в дискорд не орало, что сервер упал. */

public class CommandSetRestart extends AFMCPCommand {
    @Override
    public String getName() {
        return "setrestart";
    }

    @Override
    public String getDisplayName() {
        return "SetRestart";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.BLACK;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        References.serverRestarting = true;

        return true;
    }
}
