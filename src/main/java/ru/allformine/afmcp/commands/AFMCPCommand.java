package ru.allformine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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

    public boolean isPlayerOnly() {
        return false;
    }

    public String[] getHelp() {
        return new String[]{};
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        return true;
    }

    public void reply(CommandSender sender, String text) {
        sender.sendMessage(getCommandChatColor()+getDisplayName()+ChatColor.RESET+" > "+text);
    }

    public void sendToPlayer(Player player, String text) {
        player.sendMessage(getCommandChatColor()+getDisplayName()+ChatColor.RESET+" > "+text);
    }

    public void replyHelp(CommandSender sender) {
        for(String str : this.getHelp()) {
            sender.sendMessage(str);
        }
    }
}
