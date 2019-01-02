package ru.allformine.afmcp.Tasks;

import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.net.discord.Discord;

public class TPSWatchdog extends BukkitRunnable {
    private static int TICK_COUNT= 0;
    private static long[] TICKS= new long[600];

    private static double getTPS()
    {
        if (TICK_COUNT < 100) {
            return 20.0D;
        }
        int target = (TICK_COUNT-1 - 100) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return 100 / (elapsed / 1000.0D);
    }
    private static boolean tpsisdown = false;
    public void run() {
        TICKS[(TICK_COUNT% TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT+= 1;

        if(getTPS() < 17 && tpsisdown == false) {
            Discord.sendMessage("@everyone\nTPS опустился ниже 17!", false, "Анальная опасность!", 1);
            tpsisdown = true;
        }
        else if(getTPS() >= 17 && tpsisdown == true){
            Discord.sendMessage("TPS вернулся в норму");
            tpsisdown = false;
        }
    }
}
