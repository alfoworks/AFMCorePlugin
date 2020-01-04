package ru.alfomine.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.alfomine.afmcp.customitem.CustomItemManager;

import java.util.ArrayList;

public class CommandCustomItem extends CustomCommand {
    @Override
    public String getName() {
        return "CustomItem";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        if (args.size() > 0) {
            ItemStack item = CustomItemManager.createItemById(args.get(0));

            if (item == null) {
                sendErrorMessage(sender, "Такого предмета не существует!");

                return true;
            }

            ((Player) sender).getInventory().addItem(item);

            sendMessage(sender, "Предмет выдан!");

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
