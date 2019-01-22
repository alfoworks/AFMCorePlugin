package ru.allformine.afmcp.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.AFMCorePlugin;
import ru.allformine.afmcp.net.discord.Discord;

public class TPSWatchdog extends BukkitRunnable {
    private int TICK_COUNT = 0;
    private long[] TICKS = new long[600];
    private int BAD_TPS_COUNT = 0;
    private boolean tpsIsDown = false;
    private int WARN_TPS = AFMCorePlugin.getPlugin().getConfig().getInt("tps.alarm_if_less");

    private double getTPS() {
        if (TICK_COUNT < 100) {
            return 20.0D;
        }
        int target = (TICK_COUNT - 1 - 100) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return 100 / (elapsed / 1000.0D);
    }

    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;

        if (getTPS() < WARN_TPS) {
            BAD_TPS_COUNT += 1;
        }
        if (getTPS() < WARN_TPS && !tpsIsDown && BAD_TPS_COUNT >= AFMCorePlugin.getPlugin().getConfig().getInt("tps.bad_tps_count")) {
            Discord.sendMessage("@everyone\nTPS опустился ниже " + String.valueOf(WARN_TPS) + "!", false, "Анальная опасность!", 1);
            tpsIsDown = true;
        } else if (getTPS() >= WARN_TPS && tpsIsDown) {
            Discord.sendMessage("TPS вернулся в норму", false, "TechInfo", 1);
            tpsIsDown = false;
            BAD_TPS_COUNT = 0;
        }
    }
}
