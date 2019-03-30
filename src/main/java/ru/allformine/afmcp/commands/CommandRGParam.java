package ru.allformine.afmcp.commands;

import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;
import java.util.ArrayList;

public class CommandRGParam extends AFMCPCommand {
    @Override
    public String getName() {
        return "rgparam";
    }

    @Override
    public String getDisplayName() {
        return "RGParam";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.GREEN;
    }

    @Override
    public String[] getHelp() {
        return new String[]{"Использование команды:", "/rgparam <имя региона> <параметр> <значение>, если значение=none, то параметр удаляется", "Список параметров:", "ambient <url> - фоновая музыка", "name <имя> - кастомное имя региона, которое выводится снизу экрана."};
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if(args.size() < 3) {
            replyHelp(sender);
            return true;
        }

        Player player = (Player) sender;
        RegionManager regionManager = WGBukkit.getRegionManager(player.getWorld());

        if (regionManager == null) {
            reply(sender, "Произошла неизвестная ошибка!");
            return true;
        }

        if (regionManager.getRegion(args.get(0)) == null) {
            reply(sender, "Регион с именем "+args.get(0)+" не найден!");
            return true;
        }

        Plugin plugin = AFMCorePlugin.getPlugin();

        if (args.get(1).equalsIgnoreCase("ambient")) {
            if(args.get(2).equalsIgnoreCase("none")) {
                plugin.getConfig().set("ambient_data." + args.get(0) + ".url", null);
            } else {
                plugin.getConfig().set("ambient_data." + args.get(0) + ".url", args.get(2));
            }
        } else if (args.get(1).equalsIgnoreCase("name")) {
            if(args.get(2).equalsIgnoreCase("none")) {
                plugin.getConfig().set("rgname_data." + args.get(0) + ".name", null);
            } else {
                plugin.getConfig().set("rgname_data." + args.get(0) + ".name", String.join(" ", args.subList(2, args.size())));
            }
        } else {
            replyHelp(sender);
            return true;
        }

        reply(sender, "Значение было успешно изменено!");
        plugin.saveConfig();

        return true;
    }
}
