package ru.alfomine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DiscordNotifyTask implements Runnable {
    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (AFMCorePlugin.config.getString("playerDiscordConfirmations") != null) {
                return;
            }

            player.sendMessage(String.format("%sALFO:MINE %s> Вам нужно указать свой аккаунт в Discord (т.к. его никто не читает и никто не отписывается)", ChatColor.YELLOW, ChatColor.AQUA));
            player.sendMessage(String.format("%sЧтобы указать свой аккаунт, напишите /ds <nick>", ChatColor.GREEN));
            player.sendMessage(String.format("%sЕсли ваш ник уже записан, или вы не состоите в персонале сервера - напишите /ds none", ChatColor.RED));
            player.sendMessage(String.format("%sЕсли вас вообще нет в Discord, то с подключением: https://discord.gg/Ucrrw64", ChatColor.LIGHT_PURPLE));

            Bukkit.getConsoleSender().sendMessage("Sent notification to " + player.getName());
        }
    }
}
