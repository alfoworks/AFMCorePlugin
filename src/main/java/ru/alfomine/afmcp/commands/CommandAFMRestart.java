package ru.alfomine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginStatics;
import ru.alfomine.afmcp.RebootTask;

import java.util.ArrayList;

public class CommandAFMRestart extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        PluginStatics.isServerRebooting = true;

        sendMessage(sender, "Сервер будет перезапущен через 10 сек!");

        Bukkit.getServer().getScheduler().runTaskTimer(AFMCorePlugin.getPlugin(), new RebootTask(), 0, 200);

        return true;
    }
}
