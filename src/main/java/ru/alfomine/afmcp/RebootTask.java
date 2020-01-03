package ru.alfomine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class RebootTask extends BukkitRunnable {
    private int passedSecs = 10;

    @Override
    public void run() {
        passedSecs--;

        if (passedSecs < 4) {
            Bukkit.broadcastMessage(String.format("%sСервер перезапускается через %s%s %sсекунд.", ChatColor.RED, ChatColor.WHITE, passedSecs, ChatColor.RED));
        }

        if (passedSecs == 1) {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
            Bukkit.broadcastMessage(String.format("%s%s", ChatColor.RED, "Сервер перезапускается!"));

            this.cancel();
        }
    }
}
