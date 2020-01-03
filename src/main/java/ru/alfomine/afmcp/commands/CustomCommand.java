package ru.alfomine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomCommand implements CommandExecutor {
    @Override // Не наследовать этот метод. Вместо него - onExecute
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (this.isPlayerOnly() && !(sender instanceof Player)) {
            sendMessage(sender, "Эта команда не может быть выполнена от консоли.");

            return true;
        }

        try {
            return onExecute(sender, command, label, new ArrayList<>(Arrays.asList(args)));
        } catch (Exception e) {
            e.printStackTrace();

            sendErrorMessage(sender, String.format("Ошибка при выполнении команды \"%s\", проверьте консоль для полного стектрейса.", e.getClass().getName()));

            return true;
        }
    }

    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) throws Exception {
        return false;
    }

    public boolean isPlayerOnly() {
        return false;
    }

    public void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(String.format("%s%s %s> %s", getColor(), getName(), ChatColor.WHITE, message));
    }

    public void sendErrorMessage(CommandSender sender, String message) {
        sender.sendMessage(String.format("%s%s %s> %s", ChatColor.RED, getName(), ChatColor.GOLD, message));
    }

    public ChatColor getColor() {
        return ChatColor.GOLD;
    }

    public String getName() {
        return "SOICP Command";
    }
}
