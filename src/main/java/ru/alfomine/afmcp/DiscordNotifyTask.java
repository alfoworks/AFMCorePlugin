package ru.alfomine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class DiscordNotifyTask implements Runnable {
    @Override
    public void run() {
        ConfigurationSection section = AFMCorePlugin.config.getConfigurationSection("playerDiscordConfirmations");

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (section.getKeys(false).contains(player.getName())) {
                return;
            }

            player.sendMessage(String.format("%sALFO:MINE %s> Вам нужно указать свой аккаунт в Discord (т.к. большинство его почему-то не читает", ChatColor.YELLOW, ChatColor.AQUA));
            player.sendMessage(String.format("%sЧтобы указать свой аккаунт, напишите /ds <nick>", ChatColor.GREEN));
            player.sendMessage(String.format("%sЕсли ваш ник уже записан, или вы не состоите в персонале сервера - напишите /ds none", ChatColor.RED));
        }
    }
}
