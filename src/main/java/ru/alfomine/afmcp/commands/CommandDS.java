package ru.alfomine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import ru.alfomine.afmcp.AFMCorePlugin;

import java.util.ArrayList;

public class CommandDS extends CustomCommand {
    @Override
    public String getName() {
        return "ALFO:MINE";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.YELLOW;
    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        if (args.size() > 0) {
            if (args.get(0).equalsIgnoreCase("none")) {
                AFMCorePlugin.config.set("playerDiscordConfirmations." + sender.getName(), "Not stated/Already exists");
                AFMCorePlugin.getPlugin().saveConfig();

                sendMessage(sender, "OK");

                return true;
            }

            AFMCorePlugin.config.set("playerDiscordConfirmations." + sender.getName(), String.join(" ", args));
            AFMCorePlugin.getPlugin().saveConfig();

            sendMessage(sender, "Ваш аккаунт был записан. Спасибо.");

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }
}
