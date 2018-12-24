package ru.allformine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.allformine.afmcp.net.discord.Discord;

public class PlayerQuitJoin {
    public static void sendPlayerQuitJoinMessage(Player player, boolean act, JavaPlugin plugin) {
        boolean isStaff = player.hasPermission("afmcp.staff");
        int logLevel = 1;
        String message;

        if(act) { //true - вошел в игру, false - вышел.
            message = " вошел в игру!";
        } else {
            message = " вышел из игры!";
        }

        if(isStaff) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                if(p.hasPermission("afmcp.staff") && !p.equals(player)) {
                    p.sendMessage(ChatColor.DARK_AQUA+player.getName()+ChatColor.GREEN+message+ChatColor.DARK_AQUA+" (персонал)");
                }
            }

            message = message+" (персонал)";
            logLevel = 2;
        }

        Discord.sendMessage(message, true, player.getDisplayName(), logLevel, plugin);
    }
}
