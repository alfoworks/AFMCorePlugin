package ru.iterator.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.iterator.afmcp.AFMCorePlugin;
import ru.iterator.afmcp.PluginStatics;

import java.util.ArrayList;

public class CommandUnsetChest extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        Player player = (Player) sender;

        PluginStatics.playerChestPreset.remove(player);
        PluginStatics.playerChestSet.put(player, args.get(0));
        PluginStatics.playerDel.add(player);

        Bukkit.getScheduler().runTaskLater(AFMCorePlugin.getPlugin(), () -> PluginStatics.playerChestSet.remove(player), 600);

        sendMessage(sender, "Теперь нажмите ПКМ по сундуку для установки шаблона на него. На это у вас есть 30 сек.");

        return true;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    @Override
    public String getName() {
        return "SOICP Chest Refill";
    }

    @Override
    public ChatColor getColor() {
        return ChatColor.AQUA;
    }
}
