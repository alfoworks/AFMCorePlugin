package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import ru.allformine.afmcp.net.eco.Eco;
import java.util.ArrayList;

public class CommandTokens extends AFMCPCommand {
    @Override
    public String getName() {
        return "tokens";
    }

    @Override
    public String getDisplayName() {
        return "AFMEco";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        String playerBal = Eco.getBalance(sender.getName());

        if (playerBal == null) {
            reply(sender, "Произошла ошибка при выполнении этой команды.");
            return true;
        }

        reply(sender, "Ваш баланс: " + ChatColor.GREEN + playerBal + " токенов" + ChatColor.WHITE + ".");
        return true;
    }
}