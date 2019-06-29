package ru.allformine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.api.Webhook;

import java.util.ArrayList;

public class CommandMaintenance extends AFMCPCommand {
    @Override
    public String getName() {
        return "maintenance";
    }

    @Override
    public String getDisplayName() {
        return "Maintenance";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.BLUE;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if (args.size() > 0 && (args.get(0).equals("true") || args.get(0).equals("false"))) {
            Plugin plugin = AFMCorePlugin.getPlugin();

            if(args.get(0).equals("true"))
                Webhook.sendServerMessage(Webhook.TypeServerMessage.SERVER_MAINTENANCE);

            plugin.getConfig().set("server_maintenance.enabled", args.get(0).equals("true"));
            plugin.saveConfig();

            reply(sender, "Режим тех. работ был переключен.");

            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!p.hasPermission("afmcp.staff")) {
                    p.kickPlayer(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("server_maintenance.kickMessage")));
                }
            }
            return true;
        } else {
            return false;
        }
    }
}