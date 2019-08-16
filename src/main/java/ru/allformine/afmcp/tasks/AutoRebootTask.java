package ru.allformine.afmcp.tasks;

import ru.allformine.afmcp.Utils;

import java.util.Calendar;

public class AutoRebootTask implements Runnable {
    private boolean rebooted = false;

    @Override
    public void run() {
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minutes = rightNow.get(Calendar.MINUTE);

        if ((hour != 18 || minutes > 1) || rebooted) {
            return;
        }

        Utils.sendNotifyWithSoundToAll("Сервер перезапускается!", "Вернемся через минуту.");

        Utils.afmRestart();

        rebooted = true;
    }
}
