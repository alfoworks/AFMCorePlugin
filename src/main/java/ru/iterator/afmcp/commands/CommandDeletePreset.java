package ru.iterator.afmcp.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.iterator.afmcp.AFMCorePlugin;
import ru.iterator.afmcp.PluginStatics;

import java.util.ArrayList;

public class CommandDeletePreset extends CustomCommand {
    @Override
    public boolean onExecute(CommandSender sender, Command command, String label, ArrayList<String> args) {
        if (args.size() < 1) {
            sendMessage(sender, "Укажите имя шаблона БЕЗ пробелов.");
            return true;
        }

        Player player = (Player) sender;

        FileConfiguration config = AFMCorePlugin.config;

        PluginStatics.playerChestPreset.remove(player);

        if (config.get("presets." + args.get(0)) == null) {
            sendMessage(sender, "Шаблона с таким именем не существует.");

            return true;
        }

        config.set("presets." + args.get(0), null);
        config.set("chests." + args.get(0), null);

        AFMCorePlugin.getPlugin().saveConfig();

        sendMessage(sender, "Шаблон был удалён.");

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
