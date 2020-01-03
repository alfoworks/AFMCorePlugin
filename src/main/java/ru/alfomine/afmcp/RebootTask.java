package ru.alfomine.afmcp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Instrument;
import org.bukkit.Note;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class RebootTask extends BukkitRunnable {
    private int passedSecs = 10;

    @Override
    public void run() {
        passedSecs--;

        if (passedSecs < 4) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.playNote(player.getLocation(), Instrument.PIANO, Note.flat(1, Note.Tone.A));
            }

            Bukkit.broadcastMessage(String.format("%sСервер перезапускается через %s%s %sсекунд.", ChatColor.RED, ChatColor.WHITE, passedSecs, ChatColor.RED));
        }

        if (passedSecs == 1) {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                player.kickPlayer(ChatColor.LIGHT_PURPLE + "Сервер ушёл на рестарт! Увидимся через минуту <3");
            }

            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "stop");
            Bukkit.broadcastMessage(String.format("%s%s", ChatColor.RED, "Сервер перезапускается!"));

            this.cancel();
        }
    }
}
