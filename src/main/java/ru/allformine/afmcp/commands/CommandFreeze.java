package ru.allformine.afmcp.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.allformine.afmcp.References;

import java.util.ArrayList;

public class CommandFreeze extends AFMCPCommand {
    @Override
    public String getName() {
        return "freeze";
    }

    @Override
    public String getDisplayName() {
        return "Freeze";
    }

    @Override
    public ChatColor getCommandChatColor() {
        return ChatColor.RED;
    }

    public boolean run(ArrayList<String> args, CommandSender sender) {
        if (args.size() > 0) {
            @SuppressWarnings("deprecation")
            Player player = Bukkit.getPlayer(args.get(0)); //да мне похуй, что оно блядь не поддерживается. МНЕ ПОХУЙ!

            if (player != null) {
                if (!References.frozenPlayers.contains(player)) {
                    References.frozenPlayers.add(player);

                    reply(sender, "Вы успешно заморозили этого игрока.");
                    sendToPlayer(player, "Вас заморозили.");
                } else {
                    References.frozenPlayers.remove(player);

                    reply(sender, "Вы успешно разморозили этого игрока.");
                    sendToPlayer(player, "Вас разморозили.");
                }
            } else {
                reply(sender, "Игрок не найден.");
            }
        } else {
            return false;
        }
        return true;
    }
}
