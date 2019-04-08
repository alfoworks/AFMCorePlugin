package ru.allformine.afmcp.tasks;

import org.bukkit.scheduler.BukkitRunnable;
import ru.allformine.afmcp.net.discord.Discord;

public class TPSWatchdog extends BukkitRunnable {
    private int TICK_COUNT = 0;
    private long[] TICKS = new long[600];

    private int WARN_TPS;
    private long bad_tps_time = 0;

    private boolean tps_flag_time = false;
    private boolean notify_flag = false;

    private double getTPS() {
        if (TICK_COUNT < 100) {
            return 20.0D;
        }
        int target = (TICK_COUNT - 1 - 100) % TICKS.length;
        long elapsed = System.currentTimeMillis() - TICKS[target];

        return 100 / (elapsed / 1000.0D);
    }

    public TPSWatchdog(int warn_tps) {
        WARN_TPS = warn_tps;
    }

    @Override
    public void run() {
        TICKS[(TICK_COUNT % TICKS.length)] = System.currentTimeMillis();
        TICK_COUNT += 1;

        // =======================
        if (getTPS() < WARN_TPS) {
            if (!tps_flag_time) {
                bad_tps_time = System.currentTimeMillis() + 1000;
                tps_flag_time = true;
            }

            if (System.currentTimeMillis() >= bad_tps_time && !notify_flag) {
                Discord.sendMessageServer(Discord.MessageTypeServer.TPS_IS_BAD);

                notify_flag = true;
            }
        } else {
            if (tps_flag_time) {
                tps_flag_time = false;
                bad_tps_time = 0;
            }

            if (notify_flag) {
                notify_flag = false;
                Discord.sendMessageServer(Discord.MessageTypeServer.TPS_NORMALIZED);
            }
        }
    }
}
