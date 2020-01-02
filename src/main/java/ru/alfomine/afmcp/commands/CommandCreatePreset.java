package ru.alfomine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.alfomine.afmcp.AFMCorePlugin;
import ru.alfomine.afmcp.PluginStatics;

import java.util.ArrayList;

public class CommandCreatePreset extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        if (args.size() < 1) {
            sendMessage(sender, "Укажите имя шаблона БЕЗ пробелов.");
            return true;
        }

        Player player = (Player) sender;

        PluginStatics.playerChestSet.remove(player);
        PluginStatics.playerChestPreset.put(player, args.get(0));

        Bukkit.getScheduler().runTaskLater(AFMCorePlugin.getPlugin(), () -> PluginStatics.playerChestPreset.remove(player), 600);

        sendMessage(sender, "Теперь нажмите ПКМ по сундуку с лутом для добавления его в список шаблонов. На это у вас есть 30 сек.");

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
