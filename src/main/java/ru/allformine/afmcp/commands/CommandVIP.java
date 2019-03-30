package ru.allformine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.eco.Eco;
import java.util.ArrayList;

public class CommandVIP extends AFMCPCommand {
    @Override
    public String getName() {
        return "vip";
    }

    @Override
    public String getDisplayName() {
        return "AFMEco";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.LIGHT_PURPLE;
    }

    @Override
    public boolean isPlayerOnly() {
        return true;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        Plugin plugin = AFMCorePlugin.getPlugin();
        if (args.size() > 0 && plugin.getConfig().contains("vips." + args.get(0))) {
            String playerBal = Eco.getBalance(sender.getName());
            int cost = plugin.getConfig().getInt("vips." + args.get(0) + ".cost");

            if (playerBal == null) {
                reply(sender, "Произошла ошибка при выполнении команды.");
                return true;
            }

            if (Integer.valueOf(playerBal) < cost) {
                int needed = cost - Integer.valueOf(playerBal);

                reply(sender, ChatColor.RED
                        + "AFMEco "
                        + ChatColor.WHITE
                        + "> У вас недостаточно токенов. Вам нужно еще "
                        + ChatColor.RED
                        + needed
                        + " токенов" + ChatColor.WHITE
                        + ".");
                return true;
            }

            reply(sender, ChatColor.GREEN + "AFMEco " + ChatColor.WHITE + "> Вы успешно приобрели привелегию. Спасибо!");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), "givevip " + sender.getName() + " " + args.get(0) + " 30");

            Eco.rem(sender.getName(), String.valueOf(cost), plugin);

            return true;
        } else {
            reply(sender, ChatColor.YELLOW + "AFMEco " + ChatColor.WHITE + "> Список доступных привелегий:");

            for (String key : plugin.getConfig().getConfigurationSection("vips").getKeys(false)) {
                reply(sender, ChatColor.YELLOW
                        + key
                        + ChatColor.WHITE
                        + " - "
                        + ChatColor.YELLOW
                        + plugin.getConfig().getInt("vips." + key + ".cost")
                        + " токенов");
            }
            return true;
        }
    }
}